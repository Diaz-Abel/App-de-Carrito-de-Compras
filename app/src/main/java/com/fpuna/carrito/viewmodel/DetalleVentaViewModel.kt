package com.fpuna.carrito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.DetalleVentaDao
import com.fpuna.carrito.models.DetalleVenta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleVentaViewModel(private val detalleVentaDao: DetalleVentaDao) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<DetalleVenta>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    fun addProductToCart(producto: DetalleVenta, cantidad: Int) {
        val existingItem = _cartItems.value.find { it.idProducto == producto.idProducto }
        if (existingItem != null) {
            // Si el producto ya está en el carrito, se actualiza la cantidad
            val updatedItem = existingItem.copy(cantidad = existingItem.cantidad + cantidad)
            _cartItems.value = _cartItems.value.map { if (it.idProducto == producto.idProducto) updatedItem else it }
        } else {
            // Si no está, se agrega el nuevo producto
            _cartItems.value = _cartItems.value + producto.copy(cantidad = cantidad)
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Aquí puedes agregar más funciones según tus necesidades
}
