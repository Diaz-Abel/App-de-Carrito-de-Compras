package com.fpuna.carrito.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.fpuna.carrito.viewmodel.CategoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioView(navController: NavController, viewModel: CategoriaViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Inicio view", color = Color.White, fontWeight = FontWeight.Bold)
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
    ) {
        ContentInicioView(
            it, navController, viewModel
        )
    }
}

@Composable
fun ContentInicioView(
    it: PaddingValues,
    navController: NavController,
    viewModel: CategoriaViewModel
) {
    val state = viewModel.state
    Column(modifier = Modifier.padding(it)) {
        LazyColumn {
            items(state.listaCategorias) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(), // para que ocupe el anchor de la columna
                            horizontalArrangement = Arrangement.SpaceBetween, // espacios entre los elementos
                            verticalAlignment = Alignment.CenterVertically // centra los elementos verticalmente
                        ) {
                            IconButton(onClick = { navController.navigate("editar/${it.id}/${it.name}") }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar"
                                )
                            }
                            //Text(text = it.name, textAlign = TextAlign.Left)
                            Spacer(modifier = Modifier.width(8.dp)) // Espacio entre ícono y texto

                            Text(
                                text = it.name,
                                modifier = Modifier.weight(1f), // Ocupa el espacio restante entre los íconos
                                textAlign = TextAlign.Left // Alineado a la izquierda
                            )
                            IconButton(onClick = { viewModel.borrarCategoria(it) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Borrar"
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    Button(
        onClick = { navController.navigate("listar") }, // Navega a la lista de productos
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Ver Productos")
    }
}