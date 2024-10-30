package com.fpuna.carrito.views.producto

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarProductosView(
    navController: NavController,
    productoViewModel: ProductoViewModel,
    listaCategorias: List<Categoria>
) {
    val productos by productoViewModel.state.productosFlow.collectAsState()

    // Función para manejar la eliminación de un producto
    fun eliminarProducto(producto: Producto) {
        productoViewModel.eliminarProducto(producto.idProducto) // Llama al método para eliminar en el ViewModel
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregarProducto") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxSize()
        ) {
            if (productos.isEmpty()) {
                Text("No hay productos disponibles.")
            } else {
                LazyColumn {
                    items(productos) { producto ->
                        ProductoItem(
                            producto,
                            navController,
                            listaCategorias,
                            onDelete = { eliminarProducto(it) })
                    }
                }
            }
        }
    }
}


@Composable
fun ProductoItem(
    producto: Producto,
    navController: NavController,
    listaCategorias: List<Categoria>,
    onDelete: (Producto) -> Unit
) {
    val categoria = listaCategorias.find { it.id == producto.idCategoria }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                navController.navigate("editarProducto/${producto.idProducto}/${producto.nombre}") // Solo el ID
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Precio: ${producto.precioVenta} gs",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Categoría: ${categoria?.name ?: "Sin categoría"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                // Botón de Editar
                Button(
                    onClick = {
                        navController.navigate("editarProducto/${producto.idProducto}/${producto.nombre}")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón de Eliminar
                Button(
                    onClick = { onDelete(producto) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}
