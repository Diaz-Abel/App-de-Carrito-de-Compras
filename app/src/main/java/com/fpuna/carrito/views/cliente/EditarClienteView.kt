package com.fpuna.carrito.views.cliente


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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.viewmodel.ClienteViewModel

@Composable
fun EditarClienteView(
    navController: NavController,
    viewModel: ClienteViewModel,
    cedula: String
) {
    Scaffold() {
        ContentEditarView(it, navController, viewModel, cedula)
    }
}

@Composable
fun ContentEditarView(
    it: PaddingValues,
    navController: NavController,
    viewModel: ClienteViewModel,
    cedula: String
) {
    // Llama a getCliente cuando se crea la composición
    LaunchedEffect(cedula) {
        viewModel.getCliente(cedula)
    }

    // Observa el estado del cliente desde el ViewModel
    val cliente = viewModel.state.cliente

    // Verifica si el cliente se ha cargado antes de acceder a sus propiedades
    if (cliente != null) {
        var nombre by remember { mutableStateOf(cliente.nombre) }
        var apellido by remember { mutableStateOf(cliente.apellido) }

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
                label = { Text(text = "Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 15.dp)
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text(text = "Apellido") },
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
                        val clienteActualizado = Cliente(
                            idCliente = cliente.idCliente,
                            cedula = cliente.cedula,
                            nombre = nombre,
                            apellido = apellido
                        )
                        viewModel.actualizarCliente(clienteActualizado)
                        // Navega de regreso
                        navController.popBackStack()
                    }
                ) {
                    Text(text = "Guardar")
                }
            }


        }
    } else {
        // Mostrar un indicador de carga si el cliente no está disponible
        Text(text = "Cargando cliente...", modifier = Modifier.padding(16.dp))
    }
}
