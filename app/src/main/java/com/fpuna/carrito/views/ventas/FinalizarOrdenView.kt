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
    var clienteExistente by remember { mutableStateOf(false) }
    var mostrarCamposCliente by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var showPurchaseConfirmation by remember { mutableStateOf(false) } // Nuevo estado para el diálogo de confirmación de compra

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campo de Cédula
        TextField(
            value = clienteCedula,
            onValueChange = { clienteCedula = it },
            label = { Text("Cédula") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para verificar el cliente
        Button(
            onClick = {
                if (clienteCedula.isBlank()) {
                    alertMessage = "Por favor, ingrese su identificación"
                    showAlertDialog = true
                } else {
                    ventaViewModel.verificarCliente(clienteCedula) { cliente ->
                        if (cliente != null) {
                            clienteNombre = cliente.nombre
                            clienteApellido = cliente.apellido
                            clienteExistente = true
                        } else {
                            clienteNombre = ""
                            clienteApellido = ""
                            clienteExistente = false
                            alertMessage = "Cliente no encontrado. Por favor, ingresa el nombre y apellido."
                            showAlertDialog = true
                        }
                        mostrarCamposCliente = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Verificar Cliente")
        }

        // Muestra los campos de nombre, apellido y el botón de confirmar solo después de verificar la cédula
        if (mostrarCamposCliente) {
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = clienteNombre,
                onValueChange = { clienteNombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !clienteExistente // Solo habilitado si el cliente no existe
            )

            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = clienteApellido,
                onValueChange = { clienteApellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !clienteExistente // Solo habilitado si el cliente no existe
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val cliente = Cliente(
                        cedula = clienteCedula,
                        nombre = clienteNombre,
                        apellido = clienteApellido
                    )
                    val fechaCompra = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                    val venta = Venta(fecha = fechaCompra, idCliente = 0, total = total)

                    ventaViewModel.finalizarOrden(
                        cliente = cliente,
                        venta = venta,
                        itemsCarrito = itemsCarrito
                    )

                    showPurchaseConfirmation = true // Mostrar el mensaje de confirmación de compra
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Confirmar Orden")
            }
        }

        // Diálogo de advertencia
        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                title = { Text("Advertencia") },
                text = { Text(alertMessage) },
                confirmButton = {
                    Button(onClick = { showAlertDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        // Diálogo de confirmación de compra
        if (showPurchaseConfirmation) {
            AlertDialog(
                onDismissRequest = {
                    showPurchaseConfirmation = false
                    navController.popBackStack()
                },
                title = { Text("Compra exitosa") },
                text = { Text("Su compra ya fue procesada") },
                confirmButton = {
                    Button(onClick = {
                        showPurchaseConfirmation = false
                        navController.popBackStack() // Regresar al inicio
                    }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

