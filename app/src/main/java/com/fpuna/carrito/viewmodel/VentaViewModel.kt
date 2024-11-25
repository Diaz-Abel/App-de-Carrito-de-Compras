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
    fun filtrarVentasPorCliente(query: String) {
        viewModelScope.launch {
            // Primero, intenta encontrar el cliente por cédula
            val clientePorCedula = clienteDao.getClienteByCedula(query)

            if (clientePorCedula != null) {
                // Si encontró un cliente por cédula, carga sus ventas
                _ventasFlow.value = ventaDao.getVentasByCliente(clientePorCedula.idCliente)
            } else {
                // Si no encontró por cédula, intenta buscar por nombre
                val clientesPorNombre = clienteDao.getClientesByNombre(query)

                // Recolecta todas las ventas de clientes cuyo nombre coincide con el query
                val ventas = mutableListOf<Venta>()
                for (cliente in clientesPorNombre) {
                    ventas.addAll(ventaDao.getVentasByCliente(cliente.idCliente))
                }

                // Actualiza el flujo con las ventas encontradas
                _ventasFlow.value = ventas
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
    fun finalizarOrden(
        cliente: Cliente,
        venta: Venta,
        itemsCarrito: List<CarritoItem>,
        tipoOperacion: String, // Tipo de operación ("pickup" o "delivery")
        direccionEntrega: String? = null // Dirección opcional para delivery
    ) {
        viewModelScope.launch {
            // Paso 1: Verificar si el cliente ya existe. Si no, registrar al cliente
            val existingCliente = clienteDao.getClienteByCedula(cliente.cedula)
            val idCliente = if (existingCliente == null) {
                clienteDao.insert(cliente) // Inserta el cliente y devuelve el nuevo ID
            } else {
                existingCliente.idCliente
            }

            // Paso 2: Crear una copia de la venta con los datos adicionales
            val ventaConDatos = venta.copy(
                idCliente = idCliente, // Relaciona la venta con el ID del cliente
                tipoOperacion = tipoOperacion,
                direccionEntrega = if (tipoOperacion == "delivery") direccionEntrega else null // Solo guarda la dirección si es "delivery"
            )

            // Inserta la venta en la base de datos
            val idVenta = ventaDao.insertVenta(ventaConDatos)

            // Paso 3: Registrar los detalles de cada producto en el carrito
            itemsCarrito.forEach { item ->
                val producto = productoDao.obtenerProductoPorId(item.idProducto)
                producto?.let {
                    val detalleVenta = DetalleVenta(
                        idVenta = idVenta, // Asocia los detalles a la venta
                        idProducto = item.idProducto.toLong(),
                        cantidad = item.cantidad,
                        precio = it.precioVenta // Usa el precio del producto actual
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
