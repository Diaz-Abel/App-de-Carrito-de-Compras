package com.fpuna.carrito.views.ventas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fpuna.carrito.models.CarritoItem
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.viewmodel.CarritoViewModel
import com.fpuna.carrito.viewmodel.VentaViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

@Composable
fun CarritoView(
    navController: NavController,
    carritoViewModel: CarritoViewModel = viewModel(),
    ventaViewModel: VentaViewModel = viewModel()
) {

    // Observa los cambios en el flujo de itemsCarrito con una lista vacía inicial
    val itemsCarrito by carritoViewModel.itemsCarrito.collectAsState()
    var total by remember { mutableStateOf(0.0) }


    // Calcular el total del carrito en tiempo real, llamando a la función de suspensión en una corrutina
    LaunchedEffect(itemsCarrito) {
        val precios = itemsCarrito.map { item ->
            async {
                val producto = carritoViewModel.obtenerProductoPorId(item.idProducto)
                producto?.precioVenta?.times(item.cantidad) ?: 0.0
            }
        }
        total = precios.awaitAll().sum()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

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
                        onCantidadChange = { nuevaCantidad ->
                            carritoViewModel.actualizarCantidadProducto(
                                item.idCarritoItem,
                                nuevaCantidad
                            )
                        },
                        navController = navController,
                        producto = it
                    )
                }
            }
        }
        // Mostrar el total general del carrito
        Text(
            text = "Total: ${"%.2f".format(total)} Gs.", // Formatear a dos decimales
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = { carritoViewModel.vaciarCarrito() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Vaciar Carrito")
        }

        // Botón para navegar a FinalizarOrdenView y pasar el total y items
        Button(
            onClick = {
                navController.navigate("finalizarOrden?total=${total}") {
                    popUpTo("carrito") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Finalizar Orden")
        }
    }
}

@Composable
fun CarritoItemView(
    item: CarritoItem,
    onEliminar: () -> Unit,
    onCantidadChange: (Int) -> Unit,
    navController: NavController,
    producto: Producto
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var cantidad by remember { mutableStateOf(item.cantidad) }

    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("detalleProducto/${item.idProducto}")
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Producto: ${producto.nombre}")
            Text("Precio unitario: ${producto.precioVenta}")
            Text("Subtotal: ${"%.2f".format(cantidad * producto.precioVenta)} Gs.")
        }
        // Ajusta el espacio entre los elementos del Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedTextField(
                value = cantidad.toString(),
                onValueChange = {
                    when {
                        it.isEmpty() -> {
                            cantidad = 1
                            // No mostramos un mensaje de alerta si está vacío, solo lo ignoramos
                        }

                        it.toIntOrNull() == null || it.toInt() < 0 -> {
                            alertMessage = "Por favor, ingresa un número entero válido."
                            showAlertDialog = true // Mostrar el diálogo de alerta
                        }

                        else -> {
                            val nuevaCantidad = it.toInt()
                            cantidad = nuevaCantidad
                            onCantidadChange(nuevaCantidad) // Notificar el cambio solo si es un número válido
                        }
                    }
                },
                label = { Text("Cantidad") },
                modifier = Modifier.width(100.dp) // Ajusta el ancho según sea necesario
            )

            Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el campo y el ícono
            IconButton(onClick = { showConfirmDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar()
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
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text("Advertencia") },
            text = { Text(alertMessage) },
            confirmButton = {
                Button(onClick = { showAlertDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
