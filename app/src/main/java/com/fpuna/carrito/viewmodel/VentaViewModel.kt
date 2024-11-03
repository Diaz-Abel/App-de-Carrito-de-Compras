package com.fpuna.carrito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.ClienteDao
import com.fpuna.carrito.daos.VentaDao
import com.fpuna.carrito.daos.ProductoDao
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.DetalleVenta
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.models.CarritoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow // Import para Flow
import com.fpuna.carrito.models.DetalleVentaProducto // Import para DetalleVentaProducto


class VentaViewModel(
    private val ventaDao: VentaDao,
    private val clienteDao: ClienteDao,
    private val productoDao: ProductoDao // Agrega ProductoDao para obtener los precios
) : ViewModel() {

    // Define el método obtenerDetalleVenta en VentaViewModel
    fun obtenerDetalleVenta(idVenta: Long): Flow<List<DetalleVentaProducto>> {
        return ventaDao.getDetallesDeVenta(idVenta)
    }

    // Función para verificar si un cliente existe por su cédula
    fun verificarCliente(cedula: String, onResult: (Cliente?) -> Unit) {
        viewModelScope.launch {
            val cliente = clienteDao.getClienteByCedula(cedula)
            onResult(cliente)
        }
    }

    // Función para finalizar la orden
    fun finalizarOrden(
        cliente: Cliente,
        venta: Venta,
        itemsCarrito: List<CarritoItem>
    ) {
        viewModelScope.launch {
            // Paso 1: Verificar si el cliente ya existe, si no, registrarlo
            val existingCliente = clienteDao.getClienteByCedula(cliente.cedula)
            val idCliente = if (existingCliente == null) {
                clienteDao.insert(cliente)
            } else {
                existingCliente.idCliente
            }

            // Paso 2: Registrar la venta
            val nuevaVenta = venta.copy(idCliente = idCliente)
            val idVenta = ventaDao.insertVenta(nuevaVenta)

            // Paso 3: Registrar los detalles de cada producto en el carrito
            itemsCarrito.forEach { item ->
                val producto = productoDao.obtenerProductoPorId(item.idProducto)
                producto?.let {
                    val detalleVenta = DetalleVenta(
                        idVenta = idVenta,
                        idProducto = item.idProducto.toLong(),
                        cantidad = item.cantidad,
                        precio = it.precioVenta // Usa el precio del producto
                    )
                    ventaDao.insertDetalleVenta(detalleVenta)
                }
            }
        }
    }

    // Estado para la lista de ventas
    val ventasFlow = MutableStateFlow<List<Venta>>(emptyList())

    init {
        cargarVentas()
    }

    // Cargar todas las ventas
    fun cargarVentas() {
        viewModelScope.launch {
            ventasFlow.value = ventaDao.getAllVentas()
        }
    }

    // Filtrar ventas por fecha
    fun filtrarVentasPorFecha(fecha: String) = viewModelScope.launch {
        ventasFlow.value = ventaDao.getVentasByFecha(fecha)
    }

    // Filtrar ventas por cliente (nombre, apellido, o cédula)
    fun filtrarVentasPorCliente(cedula: String) = viewModelScope.launch {
        val cliente = clienteDao.getClienteByCedula(cedula)
        if (cliente != null) {
            ventasFlow.value = ventaDao.getVentasByCliente(cliente.idCliente)
        } else {
            ventasFlow.value = emptyList() // Cliente no encontrado
        }
    }
}
