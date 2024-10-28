package com.fpuna.carrito.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.ProductoDao
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.states.ProductoState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductoViewModel(private val dao: ProductoDao) : ViewModel() {

    var state by mutableStateOf(ProductoState())
        private set

    init {
        // Observa el LiveData
        dao.getAllProductos().observeForever { productos ->
            state = state.copy(
                // Actualiza el estado de la lista de productos
                listaProductos = productos
            )
        }
    }

    fun agregarProducto(producto: Producto) = viewModelScope.launch {
        dao.insert(producto = producto)
    }

    fun actualizarProducto(producto: Producto) = viewModelScope.launch {
        dao.update(producto = producto)
    }

    fun eliminarProducto(idProducto: Long) = viewModelScope.launch {
        dao.deleteById(idProducto)
    }

}
