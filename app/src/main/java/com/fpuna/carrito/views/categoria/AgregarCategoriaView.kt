package com.fpuna.carrito.views.categoria

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

@Composable
fun AgregarCategoriaView(navController: NavController, viewModel: CategoriaViewModel) {
    Scaffold {
        ContentAgregarCategoriaView(it, navController, viewModel)
    }
}

@Composable
fun ContentAgregarCategoriaView(
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

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para seleccionar un ícono
        Button(onClick = { showImagePicker = true }) {
            Text("Seleccionar Ícono")
        }

        // Muestra el ícono seleccionado
        if (iconUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = iconUri),
                contentDescription = "Ícono seleccionado",
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para agregar la categoría
        Button(
            onClick = {
                val categoria = Categoria(name = nombre, iconUri = iconUri)
                viewModel.agregarCategoria(categoria)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Categoría")
        }

        // ImagePicker para seleccionar el ícono
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
