package com.fpuna.carrito.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.models.Categoria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoView(
    navController: NavController,
    viewModel: ProductoViewModel,
    categorias: List<Categoria>
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Agregar Producto", color = Color.White, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        ContentAgregarProductoView(paddingValues, navController, viewModel, categorias)
    }
}

@Composable
fun ContentAgregarProductoView(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: ProductoViewModel,
    categorias: List<Categoria>
) {
    var nombre by remember { mutableStateOf("") }
    var precioVenta by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }
    var showDialog by remember { mutableStateOf(false) }

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
            label = { Text("Nombre del Producto") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )
        OutlinedTextField(
            value = precioVenta,
            onValueChange = { precioVenta = it },
            label = { Text("Precio de Venta") },
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
                        categorias.forEach { categoria ->
                            Text(
                                text = categoria.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCategoria = categoria
                                        showDialog = false
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = {
                    if (nombre.isNotEmpty() && precioVenta.isNotEmpty() && selectedCategoria != null) {
                        val producto = Producto(
                            nombre = nombre,
                            precioVenta = precioVenta.toDouble(),
                            idCategoria = selectedCategoria!!.id
                        )
                        viewModel.agregarProducto(producto)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.padding(horizontal = 30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("Agregar")
            }
        }
    }
}
