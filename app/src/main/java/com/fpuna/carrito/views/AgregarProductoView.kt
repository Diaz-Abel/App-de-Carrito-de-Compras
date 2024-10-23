package com.fpuna.carrito.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoView(navController: NavController, viewModel: ProductoViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Agregar Producto",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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
        ContentAgregarProductoView(paddingValues, navController, viewModel)
    }
}

@Composable
fun ContentAgregarProductoView(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: ProductoViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var precioVenta by remember { mutableStateOf("") }
    var idCategoria by remember { mutableStateOf("") } // Asume que tienes una forma de seleccionar la categoría

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
        // Aquí podrías agregar un selector o una lista desplegable para seleccionar la categoría

        Button(
            onClick = {
                // Asegúrate de validar los campos antes de crear el producto
                if (nombre.isNotEmpty() && precioVenta.isNotEmpty() && idCategoria.isNotEmpty()) {
                    val producto = Producto(
                        nombre = nombre,
                        precioVenta = precioVenta.toDouble(),
                        idCategoria = idCategoria.toInt() // Asegúrate de convertirlo correctamente
                    )
                    viewModel.agregarProducto(producto)
                    navController.popBackStack() // Vuelve a la vista anterior
                }
            }
        ) {
            Text(text = "Agregar")
        }
    }
}
