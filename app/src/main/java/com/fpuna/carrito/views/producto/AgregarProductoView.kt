package com.fpuna.carrito.views.producto

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.fpuna.carrito.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoView(
    navController: NavController,
    viewModel: ProductoViewModel,
    categorias: List<Categoria>
) {
    Scaffold()
    { paddingValues ->
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

    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = {

                    // Validación de entrada
                    if (nombre.isEmpty() || precioVenta.isEmpty() || selectedCategoria == null) {
                        alertMessage = "Por favor, completa todos los campos."
                        showAlertDialog = true
                    } else {
                        // Validación de precio
                        val precio = precioVenta.toDoubleOrNull()
                        if (precio == null || precio <= 0) {
                            alertMessage = "El precio debe ser un número válido mayor que cero."
                            showAlertDialog = true
                        } else {
                            // Crear el producto y agregarlo
                            val producto = Producto(
                                nombre = nombre,
                                precioVenta = precio,
                                idCategoria = selectedCategoria!!.id
                            )
                            viewModel.agregarProducto(producto)
                            navController.popBackStack()
                        }
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
