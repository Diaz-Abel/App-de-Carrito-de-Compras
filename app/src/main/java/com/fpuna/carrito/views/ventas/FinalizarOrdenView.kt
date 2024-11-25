package com.fpuna.carrito.views.ventas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.viewmodel.CarritoViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.viewmodel.VentaViewModel
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun FinalizarOrdenView(
    navController: NavController,
    ventaViewModel: VentaViewModel = viewModel(),
    total: Double,
    carritoViewModel: CarritoViewModel,
    productoViewModel: ProductoViewModel
) {
    val itemsCarrito = carritoViewModel.itemsCarrito.collectAsState(initial = emptyList()).value
    var clienteCedula by remember { mutableStateOf("") }
    var clienteNombre by remember { mutableStateOf("") }
    var clienteApellido by remember { mutableStateOf("") }
    var clienteExistente by remember { mutableStateOf(false) }
    var mostrarCamposCliente by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var showPurchaseConfirmation by remember { mutableStateOf(false) }
    var tipoOperacion by remember { mutableStateOf("pickup") } // "pickup" o "delivery"
    var direccionEntrega by remember { mutableStateOf("") } // Dirección de entrega
    var geoPoint by remember { mutableStateOf<GeoPoint?>(null) } // Punto del mapa seleccionado

    // Escuchar cambios del mapa

    navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Pair<Double, Double>>("geoPointSeleccionado")
        ?.observeForever { nuevoGeoPoint ->
            geoPoint = nuevoGeoPoint?.let { GeoPoint(it.first, it.second) }
        }




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
                            alertMessage =
                                "Cliente no encontrado. Por favor, ingresa el nombre y apellido."
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

        if (mostrarCamposCliente) {
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = clienteNombre,
                onValueChange = { clienteNombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !clienteExistente
            )

            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = clienteApellido,
                onValueChange = { clienteApellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !clienteExistente
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selección de tipo de operación
            Text("Seleccione el tipo de operación", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = { tipoOperacion = "pickup" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tipoOperacion == "pickup") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Pickup")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { tipoOperacion = "delivery" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tipoOperacion == "delivery") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Delivery")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de texto para dirección, si se selecciona "delivery"
            if (tipoOperacion == "delivery") {
                Button(
                    onClick = {
                        navController.navigate("seleccionarUbicacion") // Navega al mapa
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar ubicación en el mapa")
                }

                if (geoPoint != null) {
                    Text(
                        text = "Ubicación seleccionada: Lat ${geoPoint?.latitude}, Lng ${geoPoint?.longitude}",
                        modifier = Modifier.padding(8.dp)
                    )
                }

                TextField(
                    value = direccionEntrega,
                    onValueChange = { direccionEntrega = it },
                    label = { Text("Dirección de entrega (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val cliente = Cliente(
                        cedula = clienteCedula,
                        nombre = clienteNombre,
                        apellido = clienteApellido
                    )
                    val fechaCompra = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Calendar.getInstance().time)

                    // Usar las coordenadas seleccionadas o la dirección ingresada
                    val direccionFinal = if (tipoOperacion == "delivery") {
                        geoPoint?.let { "Lat: ${it.latitude}, Lng: ${it.longitude}" }
                    } else {
                        null // Para "pickup", no se necesita dirección
                    }

                    val venta = Venta(
                        fecha = fechaCompra,
                        idCliente = 0, // Asigna el ID del cliente correspondiente
                        total = total,
                        tipoOperacion = tipoOperacion,
                        direccionEntrega = direccionFinal
                    )

                    ventaViewModel.finalizarOrden(
                        cliente = cliente,
                        venta = venta,
                        itemsCarrito = itemsCarrito,
                        tipoOperacion = tipoOperacion,
                        direccionEntrega = direccionFinal
                    )
                    carritoViewModel.vaciarCarrito()
                    showPurchaseConfirmation = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Confirmar Orden")
            }

        }

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
                        navController.popBackStack()
                    }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}
