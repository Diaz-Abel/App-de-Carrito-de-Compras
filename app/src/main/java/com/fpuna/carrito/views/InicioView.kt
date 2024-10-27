package com.fpuna.carrito.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.style.TextAlign
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.viewmodel.CategoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioView(navController: NavController, viewModel: CategoriaViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Inicio", color = Color.White, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregar") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        ContentInicioView(paddingValues, navController, viewModel)
    }
}

@Composable
fun ContentInicioView(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: CategoriaViewModel
) {
    val state = viewModel.state

    Column(modifier = Modifier.padding(paddingValues)) {
        LazyColumn {
            items(state.listaCategorias) { categoria ->
                CategoriaItem(
                    categoria = categoria,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        // Botón para ver la lista de productos
        Button(
            onClick = { navController.navigate("listar") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Ver Productos")
        }
    }
}

@Composable
fun CategoriaItem(categoria: Categoria, navController: NavController, viewModel: CategoriaViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = categoria.name,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Left
        )
        IconButton(onClick = { navController.navigate("editar/${categoria.id}/${categoria.name}") }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
        }
        IconButton(onClick = { viewModel.borrarCategoria(categoria) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Borrar")
        }
    }
}
