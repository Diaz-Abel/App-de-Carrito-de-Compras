package com.fpuna.carrito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.ClienteDao
import com.fpuna.carrito.daos.VentaDao
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.Venta
import kotlinx.coroutines.launch

class VentaViewModel(private val ventaDao: VentaDao, private val clienteDao: ClienteDao) :
    ViewModel() {

    fun finalizarOrden(venta: Venta, cliente: Cliente) {
        viewModelScope.launch {
            val existingCliente = clienteDao.getClienteByCedula(cliente.cedula)
            if (existingCliente == null) {
                clienteDao.insertCliente(cliente)
            }
            ventaDao.insertVenta(venta)
        }
    }
}
