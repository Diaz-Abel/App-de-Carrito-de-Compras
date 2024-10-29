package com.fpuna.carrito.views.producto

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoView(
    paddingValues: PaddingValues,
    navController: NavController,
    productoViewModel: ProductoViewModel,
    categoriaViewModel: CategoriaViewModel,
    producto: Producto
) {
    var nombre by remember { mutableStateOf(producto.nombre) }
    var precioVenta by remember { mutableStateOf(producto.precioVenta.toString()) }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Obtener la lista de categorías y seleccionar la actual
    val listaCategorias = categoriaViewModel.state.listaCategorias

    // Inicializar selectedCategoria solo una vez
    LaunchedEffect(producto) {
        selectedCategoria = listaCategorias.find { it.id == producto.idCategoria }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Editar Producto", color = Color.White)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 30.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(text = "Nombre del Producto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 15.dp)
            )

            OutlinedTextField(
                value = precioVenta,
                onValueChange = { precioVenta = it },
                label = { Text(text = "Precio de Venta") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 15.dp)
            )

            // Botón para seleccionar categoría
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                )
            ) {
                Text(text = if (selectedCategoria != null) "Categoría: ${selectedCategoria!!.name}" else "Selecciona una categoría")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Seleccionar Categoría") },
                    text = {
                        Column {
                            listaCategorias.forEach { categoria ->
                                Text(
                                    text = categoria.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedCategoria =
                                                categoria // Actualiza la categoría seleccionada
                                            showDialog = false // Cierra el diálogo
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cerrar")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (nombre.isNotEmpty() && precioVenta.isNotEmpty() && selectedCategoria != null) {
                        val productoActualizado = Producto(
                            idProducto = producto.idProducto,
                            nombre = nombre,
                            precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                            idCategoria = selectedCategoria!!.id
                        )
                        productoViewModel.actualizarProducto(productoActualizado)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                Text(text = "Guardar Cambios")
            }
        }
    }
}
