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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.utils.ImagePicker
import com.fpuna.carrito.viewmodel.ProductoViewModel

@Composable
fun ListarProductosView(
    navController: NavController,
    productoViewModel: ProductoViewModel,
    listaCategorias: List<Categoria>
) {
    val productos by productoViewModel.state.productosFlow.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Observar el estado de mensaje UI (uiState)
    val uiState = productoViewModel.uiState
    var showDialog by remember { mutableStateOf(false) }

    // Mostrar diálogo si hay un mensaje en uiState
    LaunchedEffect(uiState) {
        if (uiState != null) {
            showDialog = true
        }
    }

    if (showDialog && uiState != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false; productoViewModel.uiState = null },
            title = { Text("Información") },
            text = { Text(uiState) },
            confirmButton = {
                TextButton(onClick = { showDialog = false; productoViewModel.uiState = null }) {
                    Text("Aceptar")
                }
            }
        )
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
                    val filteredProductos = productos.filter { producto ->
                        val categoria = listaCategorias.find { it.id == producto.idCategoria }
                        producto.nombre.contains(searchQuery, ignoreCase = true) ||
                                (categoria?.name?.contains(searchQuery, ignoreCase = true) == true)
                    }
                    items(filteredProductos) { producto ->
                        ProductoItem(
                            producto,
                            navController,
                            listaCategorias,
                            onDelete = { productoViewModel.eliminarProducto(it) }
                        )
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
    var showConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                navController.navigate("editarProducto/${producto.idProducto}") // Solo el ID
            }
    ) {
        Row {
            // Picker de imagen
            ImagePicker(
                initialImageUri = producto.imageUri,  // Solo se muestra la imagen
                isListMode = true            // Modo de solo mostrar
            )

            Column(
                modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Precio: ${producto.precioVenta} gs",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Cantidad disponible: ${producto.cantidadDisponible}",
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
                            navController.navigate("editarProducto/${producto.idProducto}")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Editar")
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Botón de Eliminar
                    Button(
                        onClick = { showConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(producto)
                    showConfirmDialog = false
                }) { Text("Dar de Baja Producto") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancelar") }
            },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Seguro que desea eliminar este producto?") }
        )
    }
}