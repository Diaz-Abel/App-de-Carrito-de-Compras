package com.fpuna.carrito.views.categoria

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.viewmodel.CategoriaViewModel

@Composable
fun AgregarCategoriaView(navController: NavController, viewModel: CategoriaViewModel) {
    Scaffold {
        ContentAgregarView(it, navController, viewModel)
    }
}

@Composable
fun ContentAgregarView(
    it: PaddingValues,
    navController: NavController,
    viewModel: CategoriaViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var iconUri by remember { mutableStateOf<String?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(it)
            .padding(top = 30.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo para el nombre de la categoría
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(text = "Nombre de la Categoría") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )

        // Botón para abrir el selector de imágenes
        Button(
            onClick = { showImagePicker = true },
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Seleccionar Ícono")
        }

        // Mostrar vista previa del ícono seleccionado
        if (iconUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = iconUri),
                contentDescription = "Ícono seleccionado",
                modifier = Modifier
                    .size(100.dp)
                    .padding(vertical = 16.dp)
            )
        }

        // Botón para agregar la categoría
        Button(
            onClick = {
                val categoria = Categoria(name = nombre, iconUri = iconUri)
                viewModel.agregarCategoria(categoria)
                navController.popBackStack()
            },
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Agregar Nueva Categoría")
        }

        // Mostrar el selector de imágenes si es necesario
        if (showImagePicker) {
            ImagePicker(
                onImageSelected = { uri ->
                    iconUri = uri
                    showImagePicker = false
                },
                onDismiss = { showImagePicker = false }
            )
        }
    }
}
