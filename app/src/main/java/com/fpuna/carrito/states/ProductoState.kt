package com.fpuna.carrito.states

import com.fpuna.carrito.models.Producto

data class ProductoState(
    val listaProductos: List<Producto> = emptyList()
)
