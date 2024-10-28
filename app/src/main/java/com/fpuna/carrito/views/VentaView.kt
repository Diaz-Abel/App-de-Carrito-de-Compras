package com.fpuna.carrito.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.DetalleVenta
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.viewmodel.VentaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaView(
    navController: NavController,
    productoViewModel: ProductoViewModel,
    ventaViewModel: VentaViewModel,
    listaCategorias: List<Categoria>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Producto?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var showCart by remember { mutableStateOf(false) }
    var cartItems by remember { mutableStateOf(mutableListOf<DetalleVenta>()) }

    // Filtrar productos
    val filteredProducts = productoViewModel.state.listaProductos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Módulo de Venta", style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold)

        // Buscador
        BasicTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, Color.Gray)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    innerTextField()
                    Text("Buscar productos...", color = Color.Gray)
                }
            }
        )

        // Lista de productos filtrados
        LazyColumn(modifier = Modifier.fillMaxHeight(0.35f)) {
            items(filteredProducts) { producto ->
                ProductItem(producto, listaCategorias) { selectedProduct = producto }
            }
        }

        // Selección y cantidad del producto
        selectedProduct?.let { product ->
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text("Producto: ${product.nombre}", style = MaterialTheme.typography.titleMedium)

                // Campo de entrada para la cantidad
                TextField(
                    value = if (quantity > 0) quantity.toString() else "", // Muestra vacío si quantity es 0
                    onValueChange = {
                        quantity = it.toIntOrNull() ?: 0 // Si no es un número, setea a 0
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Cantidad") } // Placeholder para indicar el propósito
                )

                // Botón para agregar al carrito
                Button(
                    onClick = {
                        val detalle = DetalleVenta(
                            idProducto = product.idProducto,
                            cantidad = quantity,
                            precio = product.precioVenta
                        )
                        cartItems.add(detalle)
                        quantity = 0 // Resetea la cantidad después de agregar
                    },
                    enabled = quantity > 0, // Botón habilitado solo si la cantidad es mayor que 0
                    modifier = Modifier.fillMaxWidth() // Hacer el botón del ancho completo
                ) {
                    Text("Agregar al carrito")
                }
            }
        }



        // Botón para ver el carrito
        Button(onClick = { showCart = true }) {
            Text("Ver carrito")
        }

        // Mostrar carrito
        if (showCart) {
            CartView(
                cartItems,
                onClose = { showCart = false },
                onCompleteOrder = { cedula, nombre, apellido ->
                    handleOrder(cedula, nombre, apellido, cartItems, ventaViewModel)
                    cartItems.clear() // Limpiar el carrito después de la compra
                    showCart = false
                }
            )
        }
    }
}

@Composable
fun ProductItem(producto: Producto, listaCategorias: List<Categoria>, onSelect: () -> Unit) {
    val categoria = listaCategorias.find { it.id == producto.idCategoria }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelect() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text(text = "Precio: ${producto.precioVenta} gs", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Categoría: ${categoria?.name ?: "Sin categoría"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun CartView(cartItems: List<DetalleVenta>, onClose: () -> Unit, onCompleteOrder: (String, String, String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var cedula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    Row(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onClose) {
            Text("Cerrar Carrito")
        }

        Button(onClick = { showDialog = true }) {
            Text("Finalizar Orden")
        }
    }
    Row(modifier = Modifier.padding(10.dp)) {
        Text("Carrito: ", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(cartItems) { item ->
                Text("Producto ID: ${item.idProducto} - Cantidad: ${item.cantidad} - Precio: ${item.precio*item.cantidad} gs")
            }
        }



        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Registrar Cliente") },
                text = {
                    Column {
                        TextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cédula") })
                        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                        TextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        onCompleteOrder(cedula, nombre, apellido)
                        showDialog = false
                    }) {
                        Text("Registrar y Finalizar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

// Función para manejar la finalización de la orden
private fun handleOrder(cedula: String, nombre: String, apellido: String, cartItems: List<DetalleVenta>, ventaViewModel: VentaViewModel) {
    // Llama a las funciones de suspensión dentro de una corutina
    val coroutineScope = CoroutineScope(Dispatchers.Main) // Usar un scope de corutina en la función
    coroutineScope.launch {
        val cliente = ventaViewModel.getClienteByCedula(cedula)
        if (cliente == null) {
            val newCliente = Cliente(cedula = cedula, nombre = nombre, apellido = apellido)
            ventaViewModel.registerCliente(newCliente)
            ventaViewModel.finalizarOrden(newCliente, cartItems)
        } else {
            ventaViewModel.finalizarOrden(cliente, cartItems)
        }
    }
}
