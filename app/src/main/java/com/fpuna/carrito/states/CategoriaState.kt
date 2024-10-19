package com.fpuna.carrito.states

import com.fpuna.carrito.models.Categoria

data class CategoriaState(
    val listaCategorias: List<Categoria> = emptyList()
)
