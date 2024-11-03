package com.fpuna.carrito.states

import com.fpuna.carrito.models.Cliente

data class ClienteState(
    val listaClientes: List<Cliente> = emptyList(),
    val cliente: Cliente? = null
)
