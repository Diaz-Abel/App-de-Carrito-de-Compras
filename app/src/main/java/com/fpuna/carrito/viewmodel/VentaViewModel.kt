package com.fpuna.carrito.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.ClienteDao
import com.fpuna.carrito.daos.ProductoDao
import com.fpuna.carrito.daos.VentaDao
import com.fpuna.carrito.models.CarritoItem
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.DetalleVenta
import com.fpuna.carrito.models.DetalleVentaProducto
import com.fpuna.carrito.models.Venta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class VentaViewModel(
    private val ventaDao: VentaDao,
    private val clienteDao: ClienteDao,
    private val productoDao: ProductoDao
) : ViewModel() {

    // Estado para la lista de ventas
    private val _ventasFlow = MutableStateFlow<List<Venta>>(emptyList())
    val ventasFlow: StateFlow<List<Venta>> = _ventasFlow

    // Inicializar y cargar las ventas
    init {
        cargarVentas()
    }

    // Cargar todas las ventas
    fun cargarVentas() {
        viewModelScope.launch {
            _ventasFlow.value = ventaDao.getAllVentas()
        }
    }

    // Convertir fecha de "dd/MM/yyyy" a "yyyy-MM-dd"
    private fun convertirFecha(fecha: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(fecha)
            outputFormat.format(date ?: return "")
        } catch (e: Exception) {
            Log.e("VentaViewModel", "Error al convertir la fecha: ${e.message}")
            ""
        }
    }

    // Filtrar ventas por fecha
    fun filtrarVentasPorFecha(fecha: String) {
        val fechaConvertida = convertirFecha(fecha) // Convertir la fecha antes de usarla
        Log.d(
            "ConsultaVentasViewModel",
            "Filtrando ventas por fecha: $fechaConvertida"
        ) // Log de la fecha convertida
        viewModelScope.launch {
            _ventasFlow.value = ventaDao.getVentasByFecha(fechaConvertida)
            val ventasFiltradas = ventaDao.getVentasByFecha(fechaConvertida)
            Log.d(
                "ConsultaVentasViewModel",
                "Ventas filtradas: $ventasFiltradas"
            ) // Log de las ventas obtenidas
            obtenerFechasEjemplo()
        }
    }

    fun obtenerFechasEjemplo() {
        viewModelScope.launch {
            val fechasEjemplo = ventaDao.getFechasDeVentas()
            Log.d("ConsultaVentasViewModel", "Fechas de ventas almacenadas: $fechasEjemplo")
        }
    }

    // Filtrar ventas por cliente (nombre, apellido, o cédula)
    fun filtrarVentasPorCliente(cedula: String) {
        viewModelScope.launch {
            val cliente = clienteDao.getClienteByCedula(cedula)
            if (cliente != null) {
                _ventasFlow.value = ventaDao.getVentasByCliente(cliente.idCliente)
            } else {
                _ventasFlow.value = emptyList() // Cliente no encontrado
            }
        }
    }

    // Obtener detalles de una venta específica
    fun obtenerDetalleVenta(idVenta: Long): Flow<List<DetalleVentaProducto>> {
        return ventaDao.getDetallesDeVenta(idVenta)
    }

    // Verificar si un cliente existe por su cédula
    fun verificarCliente(cedula: String, onResult: (Cliente?) -> Unit) {
        viewModelScope.launch {
            val cliente = clienteDao.getClienteByCedula(cedula)
            onResult(cliente)
        }
    }

    // Finalizar la orden
    fun finalizarOrden(cliente: Cliente, venta: Venta, itemsCarrito: List<CarritoItem>) {
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

    // Obtener cliente por ID
    suspend fun obtenerClientePorId(clienteId: Long): Cliente {
        return clienteDao.getClienteById(clienteId)
    }
}
