package com.fpuna.carrito.views.categoria

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun InicioCategoriaView(navController: NavController, viewModel: CategoriaViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }
    val message = viewModel.uiState

    // Mostrar diálogo si hay un mensaje en uiState
    LaunchedEffect(message) {
        if (message != null) {
            showMessage = true
        }
    }

    if (showMessage && message != null) {
        AlertDialog(
            onDismissRequest = {
                showMessage = false
                viewModel.uiState = null
            },
            title = { Text("Información") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    showMessage = false
                    viewModel.uiState = null
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregarCategoria") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxSize()
        ) {

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                val filteredCategorias = viewModel.state.listaCategorias.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }

                items(filteredCategorias) { categoria ->
                    CategoriaItem(
                        categoria = categoria,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }

    }
}
@Composable
fun CategoriaItem(
    categoria: Categoria,
    navController: NavController,
    viewModel: CategoriaViewModel
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                navController.navigate("editarCategoria/${categoria.id}/${categoria.name}")
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = categoria.name, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                // Botón de Editar
                Button(
                    onClick = {
                        navController.navigate("editarCategoria/${categoria.id}/${categoria.name}")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón de Eliminar
                Button(
                    onClick = { showConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.borrarCategoria(categoria)
                    showConfirmDialog = false
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancelar") }
            },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Seguro que quieres eliminar esta categoría?") }
        )
    }
}
