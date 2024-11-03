package com.fpuna.carrito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.ClienteDao
import com.fpuna.carrito.daos.VentaDao
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.DetalleVentaProducto
import com.fpuna.carrito.models.Venta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class VentaViewModel(private val ventaDao: VentaDao, private val clienteDao: ClienteDao) :
    ViewModel() {

    fun finalizarOrden(venta: Venta, cliente: Cliente) {
        viewModelScope.launch {
            val existingCliente = clienteDao.getClienteByCedula(cliente.cedula)
            if (existingCliente == null) {
                clienteDao.insert(cliente)
            }
            ventaDao.insertVenta(venta)
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

    fun obtenerDetalleVenta(idVenta: Long): Flow<List<DetalleVentaProducto>> {
        return ventaDao.getDetallesDeVenta(idVenta)
    }
}
