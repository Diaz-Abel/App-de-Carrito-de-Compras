package com.fpuna.carrito.views.cliente


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
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.viewmodel.ClienteViewModel

@Composable
fun AgregarClienteView(navController: NavController, viewModel: ClienteViewModel) {
    Scaffold() {
        ContentAgregarView(it, navController, viewModel)
    }
}

@Composable
fun ContentAgregarView(
    it: PaddingValues,
    navController: NavController,
    viewModel: ClienteViewModel
) {
    var nombre by remember { mutableStateOf(value = "") }
    var apellido by remember { mutableStateOf(value = "") }
    var cedula by remember { mutableStateOf(value = "") }
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
        OutlinedTextField(
            value = cedula,
            onValueChange = { cedula = it },
            label = { Text(text = "Cédula") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )
        Button(
            onClick = {
                val cliente = Cliente(cedula = cedula, nombre = nombre, apellido = apellido)
                viewModel.agregarCliente(cliente)
                // vuelve a la vista anterior
                navController.popBackStack()
            }
        ) {
            Text(text = "Agregar Nuevo Cliente")
        }
    }

}