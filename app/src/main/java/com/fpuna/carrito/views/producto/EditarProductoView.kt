package com.fpuna.carrito.views.producto

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel

@Composable
fun EditarProductoView(
    navController: NavController,
    productoViewModel: ProductoViewModel,
    categoriaViewModel: CategoriaViewModel,
    producto: Producto
) {
    val context = LocalContext.current  // Obtén el contexto usando LocalContext

    var nombre by remember { mutableStateOf(producto.nombre) }
    var precioVenta by remember { mutableStateOf(producto.precioVenta.toString()) }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }
    var imageUri by remember {
        mutableStateOf(
            producto.imageUri ?: ""
        )
    }  // Estado para la URI de la imagen


    var cantidadDisponible by remember { mutableStateOf(producto.cantidadDisponible.toString()) }

    var showDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

    // Obtener la lista de categorías y seleccionar la actual
    val listaCategorias = categoriaViewModel.state.listaCategorias

    // Inicializar selectedCategoria solo una vez
    LaunchedEffect(producto) {
        selectedCategoria = listaCategorias.find { it.id == producto.idCategoria }
    }

    Scaffold { paddingValues ->
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

            // Botón para seleccionar categoría
            CustomButton(
                text = if (selectedCategoria != null) "Categoría: ${selectedCategoria!!.name}" else "Selecciona una categoría",
                onClick = { showDialog = true }
            )

            if (showDialog) {
                CategoriaSelectionDialog(
                    categorias = listaCategorias,
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

            // Picker de imagen
            ImagePicker(
                initialImageUri = imageUri,
                onImageSelected = { newUri ->
                    imageUri = newUri  // Actualiza la URI en el estado
                },
                isListMode = false          // Modo de agregar o editar
            )

            Row(
                modifier = Modifier.padding(top = 20.dp)
            ) {
                CustomButton(
                    text = "Cancelar",
                    onClick = { navController.popBackStack() }
                )
                CustomButton(
                    text = "Guardar",
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
                            val productoActualizado = Producto(
                                idProducto = producto.idProducto,
                                nombre = nombre,
                                precioVenta = precioVenta.toDouble(),
                                idCategoria = selectedCategoria!!.id,
                                imageUri = imageUri,  // Asegúrate de pasar el valor actualizado de imageUri
                                cantidadDisponible = cantidadDisponible.toInt()
                            )
                            productoViewModel.actualizarProducto(productoActualizado)
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}

