package com.fpuna.carrito.states

import com.fpuna.carrito.models.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ProductoState(
    val listaProductos: List<Producto> = emptyList(),
    val productoSeleccionado: Producto? = null // Nuevo estado
) {
    private val _productosFlow = MutableStateFlow(listaProductos)
    val productosFlow: StateFlow<List<Producto>> = _productosFlow

    fun updateProductos(productos: List<Producto>) {
        _productosFlow.value = productos
    }
}
