package com.fpuna.carrito.views.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.utils.AlertMessageDialog
import com.fpuna.carrito.utils.CategoriaSelectionDialog
import com.fpuna.carrito.utils.CustomButton
import com.fpuna.carrito.utils.CustomOutlinedTextField
import com.fpuna.carrito.utils.ImagePicker
import com.fpuna.carrito.utils.validarCamposObligatorios
import com.fpuna.carrito.utils.validarValoresNumericos
import com.fpuna.carrito.viewmodel.ProductoViewModel

@Composable
fun AgregarProductoView(
    navController: NavController,
    viewModel: ProductoViewModel,
    categorias: List<Categoria>
) {
    Scaffold { paddingValues ->
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
    var cantidadDisponible by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }

    var showDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 30.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomOutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = "Nombre del Producto"
        )
        CustomOutlinedTextField(
            value = precioVenta,
            onValueChange = { precioVenta = it },
            label = "Precio de Venta"
        )
        CustomOutlinedTextField(
            value = cantidadDisponible,
            onValueChange = { cantidadDisponible = it },
            label = "Cantidad"
        )

        // Botón para seleccionar categoría
        CustomButton(
            text = if (selectedCategoria != null) "Categoría: ${selectedCategoria!!.name}" else "Selecciona una categoría",
            onClick = { showDialog = true },
            containerColor = MaterialTheme.colorScheme.secondary
        )

        if (showDialog) {
            CategoriaSelectionDialog(
                categorias = categorias,
                onCategoriaSelected = { selectedCategoria = it },
                onDismiss = { showDialog = false }
            )
        }

        // Diálogo de advertencia
        if (showAlertDialog) {
            AlertMessageDialog(
                message = alertMessage,
                onDismiss = { showAlertDialog = false }
            )
        }

        // Selección de imagen
        ImagePicker(
            initialImageUri = imageUri,
            onImageSelected = { newUri -> imageUri = newUri },
            isListMode = false          // Modo de agregar o editar
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomButton(
                text = "Cancelar",
                onClick = { navController.popBackStack() }
            )
            CustomButton(
                text = "Agregar",
                onClick = {
                    // Validaciones
                    val mensajeError = validarCamposObligatorios(
                        nombre,
                        precioVenta,
                        cantidadDisponible,
                        selectedCategoria
                    )
                        ?: validarValoresNumericos(precioVenta, cantidadDisponible)

                    if (mensajeError != null) {
                        alertMessage = mensajeError
                        showAlertDialog = true
                    } else {
                        // Crear y agregar el producto
                        val producto = Producto(
                            nombre = nombre,
                            precioVenta = precioVenta.toDouble(),
                            idCategoria = selectedCategoria!!.id,
                            imageUri = imageUri, // Almacena la URI como cadena
                            cantidadDisponible = cantidadDisponible.toInt()
                        )
                        viewModel.agregarProducto(producto)
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}


