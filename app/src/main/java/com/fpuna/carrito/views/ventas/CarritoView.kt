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

@Composable
fun CarritoView(navController: NavController, carritoViewModel: CarritoViewModel = viewModel()) {
    val itemsCarrito = remember { mutableStateOf(listOf<CarritoItem>()) }

    LaunchedEffect(Unit) {
        carritoViewModel.obtenerCarrito { items ->
            itemsCarrito.value = items
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Carrito", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(itemsCarrito.value) { item ->
                var producto by remember { mutableStateOf<Producto?>(null) }

                // Obtener el producto por ID de manera asíncrona
                LaunchedEffect(item.idProducto) {
                    producto = carritoViewModel.obtenerProductoPorId(item.idProducto)
                }

                // Mostrar el producto solo si está disponible
                producto?.let {
                    CarritoItemView(
                        item,
                        onEliminar = {
                            carritoViewModel.eliminarItemDelCarrito(item.idCarritoItem)
                            itemsCarrito.value = itemsCarrito.value - item
                        },
                        navController = navController,
                        producto = it
                    )
                }
            }
        }

        Button(
            onClick = { carritoViewModel.vaciarCarrito() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Vaciar Carrito")
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

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Producto: ${producto.nombre}") // Aquí necesitarás obtener el nombre del producto
            Text("Cantidad: ${item.cantidad}")
            Text("Precio unitario: ${producto.precioVenta}")
            Text("Subtotal: ${item.cantidad * producto.precioVenta}")
        }
        IconButton(onClick = onEliminar) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
        }

        // Añade un clic para navegar a la vista de detalles del producto
        // Asegúrate de tener una ruta para esto en tu NavController
        Modifier.clickable {
            navController.navigate("detalleProducto/${item.idProducto}")
        }
    }
}
