package com.fpuna.carrito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.models.DetalleVenta
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.daos.VentaDao
import com.fpuna.carrito.daos.DetalleVentaDao
import com.fpuna.carrito.daos.ClienteDao
import kotlinx.coroutines.launch
import java.util.Date

class VentaViewModel(
    private val ventaDao: VentaDao,
    private val detalleVentaDao: DetalleVentaDao,
    private val clienteDao: ClienteDao
) : ViewModel() {

    // Finaliza la orden y guarda los detalles en la base de datos
    fun finalizarOrden(cliente: Cliente, detalles: List<DetalleVenta>) {
        viewModelScope.launch {
            // Crea una nueva venta
            val nuevaVenta = Venta(fecha = Date().toString(), idCliente = cliente.idCliente, total = calcularTotal(detalles))
            // Inserta la venta y obtiene el ID generado
            val idVenta = ventaDao.insert(nuevaVenta)

            // Guarda cada detalle de la venta
            detalles.forEach { detalle ->
                detalleVentaDao.insert(detalle.copy(idVenta = idVenta))
            }
        }
    }

    // Calcula el total de la venta sumando los precios de todos los detalles
    private fun calcularTotal(detalles: List<DetalleVenta>): Double {
        return detalles.sumOf { it.precio * it.cantidad }
    }

    // Obtiene un cliente por su cédula
    suspend fun getClienteByCedula(cedula: String): Cliente? {
        return clienteDao.getClienteByCedula(cedula)
    }

    // Registra un nuevo cliente en la base de datos
    suspend fun registerCliente(cliente: Cliente) {
        clienteDao.insert(cliente)
    }

    // Verifica si un cliente existe por su cédula
    suspend fun clienteExiste(cedula: String): Boolean {
        return getClienteByCedula(cedula) != null
    }


}
