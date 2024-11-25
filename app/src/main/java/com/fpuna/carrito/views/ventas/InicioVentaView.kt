package com.fpuna.carrito.views.ventas

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.utils.ImagePicker
import com.fpuna.carrito.viewmodel.CarritoViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.viewmodel.VentaViewModel
import org.osmdroid.util.GeoPoint
import java.util.Calendar
import coil3.compose.rememberAsyncImagePainter


@Composable
fun ListarVentaProductos(
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

    // Estado para la categoría seleccionada que coincide con el filtro de búsqueda
    var selectedCategoryImage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
            .fillMaxSize()
    ) {
        // Campo de búsqueda con la imagen de la categoría
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it

                    // Verificar si la búsqueda tiene coincidencia con alguna categoría
                    val categoria = listaCategorias.find { categoria ->
                        categoria.name.contains(searchQuery, ignoreCase = true)
                    }
                    selectedCategoryImage = categoria?.icono // Asignar la imagen de la categoría si hay coincidencia
                },
                label = { Text("Buscar por nombre o categoría") },
                modifier = Modifier.weight(1f)
            )

            // Mostrar la imagen de la categoría seleccionada si hay una coincidencia
            selectedCategoryImage?.let {
                // Usamos rememberAsyncImagePainter para cargar la imagen
                Image(painter = rememberAsyncImagePainter(it), contentDescription = "Imagen de la categoría", modifier = Modifier.size(40.dp))
            }
        }

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
                    val categoria = listaCategorias.find { it.id == producto.idCategoria }
                    ProductoItem(producto, categoria?.name ?: "") {
                        selectedProducto = producto
                    }
                }
            }
        }
    }

    // Lógica del Modal para agregar al carrito (ya proporcionada previamente)...
    if (selectedProducto != null) {
        ModalCantidadProducto(
            producto = selectedProducto!!,
            cantidad = cantidad, // Pasamos la cantidad como String
            onCantidadChange = { cantidad = it.toString() },
            onConfirm = {
                val cantidadInt = cantidad.toIntOrNull() // Intentar convertir a Int
                if (cantidadInt != null && cantidadInt > 0) {
                    carritoViewModel.agregarAlCarrito(selectedProducto!!, cantidadInt)

                    // Actualizar la cantidad disponible del producto
                    selectedProducto = selectedProducto!!.copy(
                        cantidadDisponible = selectedProducto!!.cantidadDisponible - cantidadInt
                    )
                    // Actualiza el estado global del producto en ProductoViewModel
                    productoViewModel.actualizarProducto(selectedProducto!!)

                    // Limpiar el modal
                    selectedProducto = null
                    // Reinicia la cantidad después de agregar
                    cantidad = ""
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
        Row {
            // Picker de imagen
            ImagePicker(
                initialImageUri = producto.imageUri,  // Solo se muestra la imagen
                isListMode = true            // Modo de solo mostrar
            )
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
                            append("Cantidad disponible: ")
                        }
                        append("${producto.cantidadDisponible}") // Sin estilo adicional
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
}

@Composable
fun ModalCantidadProducto(
    producto: Producto,
    cantidad: String,
    onCantidadChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val cantidadInt = cantidad.toIntOrNull()
                if (cantidadInt == null || cantidadInt <= 0) {
                    errorMessage = "La cantidad debe ser un número entero válido mayor que 0."
                } else if (cantidadInt > producto.cantidadDisponible) {
                    errorMessage =
                        "La cantidad ingresada no puede ser mayor a la cantidad disponible (${producto.cantidadDisponible})."
                } else {
                    errorMessage = null // Sin errores
                    onConfirm()
                }
            }) {
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
                Text("Cantidad disponible: ${producto.cantidadDisponible}")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = onCantidadChange,
                    label = { Text("Cantidad") },
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    )
}


@Composable
fun ConsultaVentasView(
    ventaViewModel: VentaViewModel,
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        ventaViewModel.cargarVentas()
    }

    val ventas by ventaViewModel.ventasFlow.collectAsState()
    var filtroFecha by remember { mutableStateOf("") }
    var filtroCedula by remember { mutableStateOf("") }
    var filtroTipoOperacion by remember { mutableStateOf("") } // Filtro por tipo de operación
    val context = LocalContext.current
    val clientes = remember { mutableStateMapOf<Long, Cliente>() }
    val tiposOperacion = listOf("Todos", "delivery", "pickup") // Opciones de tipo de operación

    LaunchedEffect(ventas) {
        for (venta in ventas) {
            if (!clientes.contains(venta.idCliente)) {
                val cliente = ventaViewModel.obtenerClientePorId(venta.idCliente)
                clientes[venta.idCliente] = cliente
            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Filtro por fecha
        Text(
            text = "Filtrar por Fecha",
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDatePicker(context) { selectedDate ->
                        filtroFecha = selectedDate
                        ventaViewModel.filtrarVentasPorFecha(filtroFecha)
                    }
                }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = "Seleccionar Fecha",
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = if (filtroFecha.isNotEmpty()) filtroFecha else "Fecha",
                color = if (filtroFecha.isNotEmpty()) Color.Black else Color.Gray,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filtro por Tipo de Operación
        Text("Filtrar por Tipo de Operación", style = MaterialTheme.typography.bodySmall)

        // Filtro por tipo de operación
        var expanded by remember { mutableStateOf(false) }
        var anchorView: View? = null // Variable para referencia al Row

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 8.dp)
                    .onGloballyPositioned { coordinates ->
                        // Obtenemos las coordenadas globales en la raíz de la pantalla
                        val position = coordinates.positionInRoot()
                        // Ahora puedes utilizar `position` si necesitas ajustar el DropdownMenu
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalShipping,
                    contentDescription = "Seleccionar Tipo de Operación",
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = if (filtroTipoOperacion.isEmpty()) "Todos" else filtroTipoOperacion,
                    color = if (filtroTipoOperacion.isEmpty()) Color.Gray else Color.Black,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // DropdownMenu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(
                    x = 0.dp,
                    y = 10.dp
                ) // Desplaza el menú hacia abajo si es necesario
            ) {
                tiposOperacion.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(text = tipo) },
                        onClick = {
                            filtroTipoOperacion = if (tipo == "Todos") "" else tipo
                            expanded = false
                            ventaViewModel.filtrarVentasPorTipoOperacion(filtroTipoOperacion)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filtro por cédula
        TextField(
            value = filtroCedula,
            onValueChange = {
                filtroCedula = it
                ventaViewModel.filtrarVentasPorCliente(filtroCedula)
            },
            label = { Text("Buscar por nombre o cédula") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de ventas
        LazyColumn {
            items(ventas) { venta ->
                val cliente = clientes[venta.idCliente]
                if (cliente != null) {
                    VentaItem(
                        venta = venta,
                        cliente = cliente,
                        onClick = {
                            navController.navigate("detalleVenta/${venta.idVenta}")
                        },
                        onVerMapaClick = { geoPoint ->
                            geoPoint?.let {
                                navController.navigate("verMapa/${it.latitude}/${it.longitude}")
                            }
                        }
                    )
                }
            }
        }
    }
}


fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            // Formato de la fecha seleccionada como "dd/MM/yyyy"
            val selectedDate =
                String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            onDateSelected(selectedDate)
        },
        year,
        month,
        day
    ).show()
}


@Composable
fun VentaItem(venta: Venta, cliente: Cliente, onClick: () -> Unit, onVerMapaClick: (GeoPoint?) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Fecha: ${venta.fecha}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Total: ${venta.total}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Nombre: ${cliente.nombre} ${cliente.apellido}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Cédula: ${cliente.cedula}",
                style = MaterialTheme.typography.titleMedium
            )

            // Mostrar solo si la venta es de tipo delivery y tiene dirección
            if (venta.tipoOperacion == "delivery" && venta.direccionEntrega != null) {
                if (venta.direccionOpcional != null) {
                    Text(
                        "Dirección Opcional: ${venta.direccionOpcional}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    "Dirección Exacta: ${venta.direccionEntrega}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val coordenadas = venta.direccionEntrega.extractCoordinates()
                        onVerMapaClick(coordenadas)
                    }
                ) {
                    Text("Ver en Mapa")
                }
            }
        }
    }
}

// Función para extraer coordenadas desde un texto en formato "Lat: x, Lng: y"
fun String.extractCoordinates(): GeoPoint? {
    val regex = Regex("Lat: ([\\-\\d.]+), Lng: ([\\-\\d.]+)")
    val matchResult = regex.find(this)
    return matchResult?.let {
        val (latitude, longitude) = it.destructured
        GeoPoint(latitude.toDouble(), longitude.toDouble())
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