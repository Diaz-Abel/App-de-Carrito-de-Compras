package com.fpuna.carrito.views.ventas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.viewmodel.VentaViewModel

@Composable
fun VentaView(navController: NavController, ventaViewModel: VentaViewModel) {
    var clienteCedula by remember { mutableStateOf("") }
    var clienteNombre by remember { mutableStateOf("") }
    var clienteApellido by remember { mutableStateOf("") }
    var total by remember { mutableStateOf(0.0) }
    var productosEnCarrito by remember { mutableStateOf(listOf<Producto>()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Lógica para abrir el buscador de productos
            }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Aquí se mostrarían los productos en el carrito
            // y permitir al usuario ingresar datos del cliente
            TextField(
                value = clienteCedula,
                onValueChange = { clienteCedula = it },
                label = { Text("Cédula") }
            )
            TextField(
                value = clienteNombre,
                onValueChange = { clienteNombre = it },
                label = { Text("Nombre") }
            )
            TextField(
                value = clienteApellido,
                onValueChange = { clienteApellido = it },
                label = { Text("Apellido") }
            )
            Button(onClick = {
                val venta = Venta(fecha = "2024-10-30", idCliente = clienteCedula.toLong(), total = total)
                val cliente = Cliente(
                    cedula = clienteCedula,
                    nombre = clienteNombre,
                    apellido = clienteApellido
                )
                ventaViewModel.finalizarOrden(venta, cliente)
            }) {
                Text("Finalizar Orden")
            }
        }
    }
}
