package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// define una entidad de datos
@Entity
data class Categoria(
    val id: Int = 0,
    val name: String,
    val iconUri: String? = null // Nueva propiedad para almacenar la URI del ícono
)

