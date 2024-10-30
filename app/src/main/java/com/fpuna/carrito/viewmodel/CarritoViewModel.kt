package com.fpuna.carrito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.CarritoDao
import com.fpuna.carrito.models.CarritoItem
import com.fpuna.carrito.models.Producto
import kotlinx.coroutines.launch

class CarritoViewModel(private val carritoDao: CarritoDao) : ViewModel() {

    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        val carritoItem = CarritoItem(
            idProducto = producto.idProducto,
            cantidad = cantidad
        )

        viewModelScope.launch {
            carritoDao.insertCarritoItem(carritoItem)
        }
    }

    // Para obtener los items del carrito
    fun obtenerCarrito(onResult: (List<CarritoItem>) -> Unit) {
        viewModelScope.launch {
            val items = carritoDao.getAllCarritoItems()
            onResult(items)
        }
    }

    fun eliminarItemDelCarrito(idCarritoItem: Long) {
        viewModelScope.launch {
            carritoDao.deleteCarritoItem(idCarritoItem)
        }
    }

    fun vaciarCarrito() {
        viewModelScope.launch {
            carritoDao.clearCarrito()
        }
    }

    // Método para obtener el producto por ID de manera asíncrona
    suspend fun obtenerProductoPorId(id: Int): Producto {
        return carritoDao.getProductosById(id)
    }
}