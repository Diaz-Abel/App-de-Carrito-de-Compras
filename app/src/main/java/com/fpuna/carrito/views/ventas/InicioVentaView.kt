package com.fpuna.carrito.views.ventas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.viewmodel.CarritoViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.viewmodel.VentaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarVentaProductos(
    navController: NavController,  // Agregar el NavController aquí
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel,
    listaCategorias: List<Categoria>
) {
    val productos by productoViewModel.state.productosFlow.collectAsState()
    var searchQuery by remember { mutableStateOf("") } // Estado para el texto de búsqueda

    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var cantidad: String by remember { mutableStateOf("") } // cantidad es un String inicialmente vacío

    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
            .fillMaxSize()
    ) {
        // Campo de búsqueda
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar por nombre o categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        if (productos.isEmpty()) {
            Text("No hay productos disponibles.")
        } else {
            LazyColumn {
                // Filtrar la lista de productos
                val filteredProductos = productos.filter { producto ->
                    // Encontrar la categoría correspondiente al producto
                    val categoria = listaCategorias.find { it.id == producto.idCategoria }
                    producto.nombre.contains(searchQuery, ignoreCase = true) ||
                            (categoria?.name?.contains(searchQuery, ignoreCase = true) == true)
                }
                items(filteredProductos) { producto ->
                    // Encontrar la categoría correspondiente al producto
                    val categoria = listaCategorias.find { it.id == producto.idCategoria }
                    ProductoItem(producto, categoria?.name ?: "") {
                        selectedProducto = producto
                    }
                }
            }
        }
    }

    if (selectedProducto != null) {
        ModalCantidadProducto(
            producto = selectedProducto!!,
            cantidad = cantidad, // Pasamos la cantidad como String
            onCantidadChange = { cantidad = it.toString() },
            onConfirm = {
                val cantidadInt = cantidad.toIntOrNull() // Intentar convertir a Int
                if (cantidadInt != null && cantidadInt > 0) {
                    carritoViewModel.agregarAlCarrito(selectedProducto!!, cantidadInt)
                    selectedProducto = null
                    cantidad = "" // Reinicia la cantidad después de agregar
                } else {
                    alertMessage = "La cantidad debe ser un número entero válido mayor que cero."
                    showAlertDialog = true
                }
            },
            onDismiss = { selectedProducto = null }
        )
    }
    // Diálogo de advertencia
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text("Advertencia") },
            text = { Text(alertMessage) },
            confirmButton = {
                TextButton(onClick = { showAlertDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    nombreCategoria: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { // Estilo solo para "Producto:"
                        append("Producto: ")
                    }
                    append(producto.nombre) // Sin estilo adicional
                },
                style = MaterialTheme.typography.bodyMedium // Estilo general para el texto
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { // Estilo solo para "Precio:"
                        append("Precio: ")
                    }
                    append("${producto.precioVenta} gs") // Sin estilo adicional
                },
                style = MaterialTheme.typography.bodyMedium // Estilo general para el texto
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { // Estilo solo para "Categoría:"
                        append("Categoría: ")
                    }
                    append(nombreCategoria) // Sin estilo adicional
                },
                style = MaterialTheme.typography.bodyMedium // Estilo general para el texto
            )
        }

    }
}

@Composable
fun ModalCantidadProducto(
    producto: Producto,
    cantidad: String,
    onCantidadChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Agregar al carrito")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Agregar ${producto.nombre} al carrito") },
        text = {
            Column {
                Text("Cantidad:")
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = {
                        val newValue = it.toIntOrNull()
                        if (newValue != null && newValue > 0) {
                            onCantidadChange(newValue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun ConsultaVentasView(
    ventaViewModel: VentaViewModel,
    navController: NavHostController
) {
    val ventas by ventaViewModel.ventasFlow.collectAsState()
    var filtroFecha by remember { mutableStateOf("") }
    var filtroCedula by remember { mutableStateOf("") }

    Column {
        // Filtro por fecha
        OutlinedTextField(
            value = filtroFecha,
            onValueChange = {
                filtroFecha = it
                if (filtroFecha.isNotEmpty()) {
                    ventaViewModel.filtrarVentasPorFecha(filtroFecha)
                } else {
                    ventaViewModel.cargarVentas() // Cargar todas las ventas si se elimina el filtro
                }
            },
            label = { Text("Filtrar por Fecha") }
        )

        // Filtro por cliente
        OutlinedTextField(
            value = filtroCedula,
            onValueChange = {
                filtroCedula = it
                if (filtroCedula.isNotEmpty()) {
                    ventaViewModel.filtrarVentasPorCliente(filtroCedula)
                } else {
                    ventaViewModel.cargarVentas() // Cargar todas las ventas si se elimina el filtro
                }
            },
            label = { Text("Filtrar por Cédula de Cliente") }
        )

        // Listado de ventas
        LazyColumn {
            items(ventas) { venta ->
                VentaItem(venta, onClick = {
                    navController.navigate("detalleVenta/${venta.idVenta}")
                })
            }
        }
    }
}

@Composable
fun VentaItem(venta: Venta, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text("Fecha: ${venta.fecha}")
        Spacer(modifier = Modifier.width(16.dp))
        Text("Total: ${venta.total}")
        Spacer(modifier = Modifier.width(16.dp))

        // Convierte idCliente a String en caso de ser necesario
        Text("Cliente ID: ${venta.idCliente.toString()}")
    }
}

@Composable
fun DetalleVentaView(
    ventaViewModel: VentaViewModel,
    idVenta: Long
) {
    val detalles by ventaViewModel.obtenerDetalleVenta(idVenta)
        .collectAsState(initial = emptyList())

    LazyColumn {
        items(detalles) { detalle ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Producto: ${detalle.nombre}")
                Spacer(modifier = Modifier.width(16.dp))
                Text("Cantidad: ${detalle.cantidad}")
                Spacer(modifier = Modifier.width(16.dp))
                Text("Precio: ${detalle.precio}")
            }
        }
    }
}