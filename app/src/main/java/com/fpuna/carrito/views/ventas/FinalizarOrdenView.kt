package com.fpuna.carrito.views.ventas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fpuna.carrito.models.CarritoItem
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.viewmodel.VentaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FinalizarOrdenView(
    navController: NavController,
    ventaViewModel: VentaViewModel = viewModel(),

    total: Double,
    itemsCarrito: List<CarritoItem>
) {
    var clienteCedula by remember { mutableStateOf("") }
    var clienteNombre by remember { mutableStateOf("") }
    var clienteApellido by remember { mutableStateOf("") }

    // Obtener la fecha actual en el formato deseado usando Calendar y SimpleDateFormat
    val fechaCompra = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
            val cliente = Cliente(
                cedula = clienteCedula,
                nombre = clienteNombre,
                apellido = clienteApellido
            )
            val venta = Venta(fecha = fechaCompra, idCliente = 0, total = total)

            ventaViewModel.finalizarOrden(
                cliente = cliente,
                venta = venta,
                itemsCarrito = itemsCarrito
            )


            navController.popBackStack()
        }) {
            Text("Confirmar Orden")
        }
    }
}
