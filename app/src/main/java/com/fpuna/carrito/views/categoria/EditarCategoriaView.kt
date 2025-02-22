package com.fpuna.carrito.views.categoria


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun EditarCategoriaView(
    navController: NavController,
    viewModel: CategoriaViewModel,
    id: Int,
    name: String
) {
    Scaffold() {
        ContentEditarView(it, navController, viewModel, id, name)
    }
}

@Composable
fun ContentEditarView(
    it: PaddingValues,
    navController: NavController,
    viewModel: CategoriaViewModel,
    id: Int,
    name: String
) {
    var nombre by remember { mutableStateOf(value = name) }
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
        Row {
            // Botón de Cancelar
            Button(
                onClick = {
                    // Cierra y vuelve a la vista anterior sin guardar cambios
                    navController.popBackStack()
                }
            ) {
                Text(text = "Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    val categoria = Categoria(id = id, name = nombre)
                    viewModel.actualizarCategoria(categoria)
                    // cierra y vuelve a la vista anterior
                    navController.popBackStack()
                }
            ) {
                Text(text = "Guardar")
            }
        }
    }
}