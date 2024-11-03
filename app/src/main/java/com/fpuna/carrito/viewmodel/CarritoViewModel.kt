package com.fpuna.carrito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.CarritoDao
import com.fpuna.carrito.models.CarritoItem
import com.fpuna.carrito.models.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarritoViewModel(private val carritoDao: CarritoDao) : ViewModel() {

    // StateFlow para gestionar el estado en vivo del carrito
    private val _itemsCarrito = MutableStateFlow<List<CarritoItem>>(emptyList())
    val itemsCarrito: StateFlow<List<CarritoItem>> = _itemsCarrito.asStateFlow()

    init {
        viewModelScope.launch {
            carritoDao.getItemsFlow().collect { items ->
                _itemsCarrito.value = items
            }
        }
    }

    // Método para agregar productos al carrito, agrupando si ya existe el mismo producto
    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        viewModelScope.launch {
            val carritoItemExistente = carritoDao.getCarritoItemByIdProducto(producto.idProducto)
            if (carritoItemExistente != null) {
                val nuevaCantidad = carritoItemExistente.cantidad + cantidad
                val itemActualizado = carritoItemExistente.copy(cantidad = nuevaCantidad)
                carritoDao.insertCarritoItem(itemActualizado)
            } else {
                val carritoItem = CarritoItem(idProducto = producto.idProducto, cantidad = cantidad)
                carritoDao.insertCarritoItem(carritoItem)
            }
            actualizarCarrito() // Actualizar la lista del carrito después de agregar
        }
    }

    // Método para cargar los items del carrito y actualizar el flujo
    fun actualizarCarrito() {
        viewModelScope.launch {
            _itemsCarrito.value = carritoDao.getAllCarritoItems()
        }
    }

    // Método para vaciar el carrito y actualizar el estado
    fun vaciarCarrito() {
        viewModelScope.launch {
            carritoDao.clearCarrito()
            _itemsCarrito.value = emptyList() // Actualizar el flujo a lista vacía
        }
    }

    // Método para eliminar un item del carrito y actualizar el flujo
    fun eliminarItemDelCarrito(idCarritoItem: Long) {
        viewModelScope.launch {
            carritoDao.deleteCarritoItem(idCarritoItem)
            actualizarCarrito() // Actualizar la lista del carrito después de eliminar
        }
    }

    // Método para obtener un producto específico por ID de manera asíncrona
    suspend fun obtenerProductoPorId(id: Int): Producto {
        return carritoDao.getProductosById(id)
    }

    // Método adicional para obtener items del carrito sin observar el StateFlow
    fun obtenerCarrito(onResult: (List<CarritoItem>) -> Unit) {
        viewModelScope.launch {
            val items = carritoDao.getAllCarritoItems()
            onResult(items)
        }
    }

    // Método para actualizar la cantidad de un producto en el carrito
    fun actualizarCantidadProducto(idCarritoItem: Long, nuevaCantidad: Int) {
        viewModelScope.launch {
            val carritoItemExistente = carritoDao.getCarritoItemById(idCarritoItem)
            if (carritoItemExistente != null) {
                // Solo actualiza si la nueva cantidad es mayor a 0
                if (nuevaCantidad > 0) {
                    val itemActualizado = carritoItemExistente.copy(cantidad = nuevaCantidad)
                    carritoDao.insertCarritoItem(itemActualizado)
                } else {
                    // Si la nueva cantidad es 0, eliminar el item del carrito
                    eliminarItemDelCarrito(idCarritoItem)
                }
                actualizarCarrito() // Actualizar la lista del carrito después de actualizar
            }
        }
    }
}
