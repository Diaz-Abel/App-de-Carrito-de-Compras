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

@Composable
fun AgregarCategoriaView(navController: NavController, viewModel: CategoriaViewModel) {
    Scaffold() {
        ContentAgregarView(it, navController, viewModel)
    }
}

@Composable
fun ContentAgregarView(
    it: PaddingValues,
    navController: NavController,
    viewModel: CategoriaViewModel
) {
    var nombre by remember { mutableStateOf(value = "") }
    Column(
        modifier = Modifier
            .padding(it)
            .padding(top = 30.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(text = "Categoria") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )
        Button(
            onClick = {
                val categoria = Categoria(name = nombre)
                viewModel.agregarCategoria(categoria)
                // vuelve a la vista anterior
                navController.popBackStack()
            }
        ) {
            Text(text = "Agregar")
        }
    }

}