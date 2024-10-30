package com.fpuna.carrito.views.ventas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.viewmodel.CarritoViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarVentaProductos(
    navController: NavController,  // Agregar el NavController aquí
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel
) {
    val productos by productoViewModel.state.productosFlow.collectAsState()
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var cantidad by remember { mutableStateOf(1) }

    LazyColumn {
        items(productos) { producto ->
            ProductoItem(producto) {
                selectedProducto = producto
            }
        }
    }

    if (selectedProducto != null) {
        ModalCantidadProducto(
            producto = selectedProducto!!,
            cantidad = cantidad,
            onCantidadChange = { cantidad = it },
            onConfirm = {
                carritoViewModel.agregarAlCarrito(selectedProducto!!, cantidad)
                selectedProducto = null
                cantidad = 1 // Reinicia la cantidad después de agregar
            },
            onDismiss = { selectedProducto = null }
        )
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Precio: ${producto.precioVenta} gs",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ModalCantidadProducto(
    producto: Producto,
    cantidad: Int,
    onCantidadChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Agregar al carrito")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Agregar ${producto.nombre} al carrito") },
        text = {
            Column {
                Text("Cantidad:")
                OutlinedTextField(
                    value = cantidad.toString(),
                    onValueChange = {
                        val newValue = it.toIntOrNull()
                        if (newValue != null && newValue > 0) {
                            onCantidadChange(newValue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
