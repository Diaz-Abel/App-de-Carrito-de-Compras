package com.fpuna.carrito.views.ventas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fpuna.carrito.models.CarritoItem
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.viewmodel.CarritoViewModel
import com.fpuna.carrito.viewmodel.VentaViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.models.DetalleVenta

@Composable
fun CarritoView(
    navController: NavController,
    carritoViewModel: CarritoViewModel = viewModel(),
    ventaViewModel: VentaViewModel = viewModel()
) {
    // Observa los cambios en el flujo de itemsCarrito con una lista vacía inicial
    val itemsCarrito by carritoViewModel.itemsCarrito.collectAsState(initial = emptyList())

    var mostrarFormularioCliente by remember { mutableStateOf(false) }
    var clienteCedula by remember { mutableStateOf("") }
    var clienteNombre by remember { mutableStateOf("") }
    var clienteApellido by remember { mutableStateOf("") }
    var total by remember { mutableStateOf(0.0) }

    // Calcular el total del carrito en tiempo real, llamando a la función de suspensión en una corrutina
    LaunchedEffect(itemsCarrito) {
        total = itemsCarrito.sumOf { item ->
            val producto = carritoViewModel.obtenerProductoPorId(item.idProducto)
            producto?.precioVenta?.times(item.cantidad) ?: 0.0
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Carrito", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(itemsCarrito) { item ->
                var producto by remember { mutableStateOf<Producto?>(null) }

                LaunchedEffect(item.idProducto) {
                    producto = carritoViewModel.obtenerProductoPorId(item.idProducto)
                }

                producto?.let {
                    CarritoItemView(
                        item = item,
                        onEliminar = {
                            carritoViewModel.eliminarItemDelCarrito(item.idCarritoItem)
                        },
                        navController = navController,
                        producto = it
                    )
                }
            }
        }

        // Botón para vaciar carrito
        Button(
            onClick = { carritoViewModel.vaciarCarrito() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Vaciar Carrito")
        }

        // Botón para finalizar orden
        Button(
            onClick = { mostrarFormularioCliente = true },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Finalizar Orden")
        }

        // Formulario de datos del cliente al finalizar la orden
        if (mostrarFormularioCliente) {
            FinalizarOrdenDialog(
                onDismiss = { mostrarFormularioCliente = false },
                onFinalizarOrden = { cedula, nombre, apellido ->
                    carritoViewModel.viewModelScope.launch {
                        finalizarOrden(
                            cedula = cedula,
                            nombre = nombre,
                            apellido = apellido,
                            ventaViewModel = ventaViewModel,
                            itemsCarrito = itemsCarrito,
                            total = total
                        )
                        mostrarFormularioCliente = false
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

@Composable
fun CarritoItemView(
    item: CarritoItem,
    onEliminar: () -> Unit,
    navController: NavController,
    producto: Producto
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("detalleProducto/${item.idProducto}")
            }, // Navega a la vista de detalles al hacer clic en toda la fila
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Producto: ${producto.nombre}")
            Text("Cantidad: ${item.cantidad}")
            Text("Precio unitario: ${producto.precioVenta}")
            Text("Subtotal: ${item.cantidad * producto.precioVenta}")
        }

        IconButton(onClick = { showConfirmDialog = true }) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
        }
    }

    // Dialogo de confirmación de eliminación
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar() // Ejecuta la función de eliminación proporcionada
                    showConfirmDialog = false
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancelar") }
            },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Seguro que quieres eliminar este producto del carrito?") }
        )
    }
}

@Composable
fun FinalizarOrdenDialog(
    onDismiss: () -> Unit,
    onFinalizarOrden: (cedula: String, nombre: String, apellido: String) -> Unit
) {
    var cedula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    var cedulaError by remember { mutableStateOf(false) }
    var nombreError by remember { mutableStateOf(false) }
    var apellidoError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // Validación de los campos antes de finalizar la orden
                cedulaError = !cedula.matches("\\d+".toRegex()) // Solo dígitos para cédula
                nombreError = nombre.isEmpty() // No debe estar vacío
                apellidoError = apellido.isEmpty() // No debe estar vacío

                // Si no hay errores, finaliza la orden
                if (!cedulaError && !nombreError && !apellidoError) {
                    onFinalizarOrden(cedula, nombre, apellido)
                }
            }) {
                Text("Finalizar Orden")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Datos del Cliente") },
        text = {
            Column {
                // Campo Cédula con validación
                OutlinedTextField(
                    value = cedula,
                    onValueChange = {
                        cedula = it
                        cedulaError = !cedula.matches("\\d+".toRegex()) // Solo dígitos
                    },
                    isError = cedulaError,
                    label = { Text("Cédula") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (cedulaError) {
                    Text("La cédula debe contener solo números", color = Color.Red, fontSize = 12.sp)
                }

                // Campo Nombre con validación
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        nombreError = nombre.isEmpty() // No debe estar vacío
                    },
                    isError = nombreError,
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (nombreError) {
                    Text("El nombre es obligatorio", color = Color.Red, fontSize = 12.sp)
                }

                // Campo Apellido con validación
                OutlinedTextField(
                    value = apellido,
                    onValueChange = {
                        apellido = it
                        apellidoError = apellido.isEmpty() // No debe estar vacío
                    },
                    isError = apellidoError,
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (apellidoError) {
                    Text("El apellido es obligatorio", color = Color.Red, fontSize = 12.sp)
                }
            }
        }
    )
}

fun finalizarOrden(
    cedula: String,
    nombre: String,
    apellido: String,
    ventaViewModel: VentaViewModel,
    itemsCarrito: List<CarritoItem>,
    total: Double
) {
    // Crear cliente y venta en el contexto de corrutina
    ventaViewModel.viewModelScope.launch {
        val cliente = Cliente(cedula = cedula, nombre = nombre, apellido = apellido)
        val clienteExistente = ventaViewModel.obtenerClientePorCedula(cedula)

        val idCliente = if (clienteExistente == null) {
            ventaViewModel.registrarCliente(cliente)
        } else {
            clienteExistente.idCliente
        }

        val venta = Venta(fecha = "2024-10-30", idCliente = idCliente, total = total)
        val idVenta = ventaViewModel.registrarVenta(venta)

        itemsCarrito.forEach { item ->
            val producto = ventaViewModel.obtenerProductoPorId(item.idProducto)
            producto?.let {
                ventaViewModel.registrarDetalleVenta(
                    DetalleVenta(
                        idVenta = idVenta,
                        idProducto = item.idProducto.toLong(),
                        cantidad = item.cantidad,
                        precio = it.precioVenta
                    )
                )
            }
        }
        ventaViewModel.vaciarCarrito() // Limpia el carrito después de registrar la venta
    }
}