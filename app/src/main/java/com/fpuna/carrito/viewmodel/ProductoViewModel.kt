package com.fpuna.carrito.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.ProductoDao
import com.fpuna.carrito.models.CarritoItem
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.states.ProductoState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductoViewModel(private val dao: ProductoDao) : ViewModel() {

    var state by mutableStateOf(ProductoState())
        private set

    var uiState by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            dao.getAllProductos().collectLatest { productos ->
                state = state.copy(listaProductos = productos)
            }
        }
    }

    fun agregarProducto(producto: Producto) = viewModelScope.launch {
        dao.insert(producto = producto)
        uiState = "Producto agregado exitosamente"
    }

    fun actualizarProducto(producto: Producto) = viewModelScope.launch {
        dao.update(producto = producto)
        uiState = "Producto actualizado exitosamente"
    }

    fun eliminarProducto(producto: Producto) = viewModelScope.launch {
        try {
            dao.deleteById(producto.idProducto)
            uiState = "Producto eliminado exitosamente"
        } catch (e: SQLiteConstraintException) {
            uiState = "No se puede eliminar este producto porque está asociado a ventas."
        } catch (e: Exception) {
            uiState = "Error al eliminar el producto"
        }
    }

    fun buscarProductoPorId(id: Int) = viewModelScope.launch {
        val producto = dao.obtenerProductoPorId(id)
        if (producto != null) {
            state = state.copy(productoSeleccionado = producto)
        } else {
            uiState = "Producto no encontrado."
        }
    }

    fun procesarCompra(itemsCarrito: List<CarritoItem>) = viewModelScope.launch {
        try {
            itemsCarrito.forEach { item ->
                val producto = dao.obtenerProductoPorId(item.idProducto)
                if (producto != null) {
                    producto.cantidadDisponible -= item.cantidad
                    dao.update(producto)
                }
            }
            uiState = "Compra procesada exitosamente."
        } catch (e: Exception) {
            uiState = "Error al procesar la compra: ${e.message}"
        }
    }
}