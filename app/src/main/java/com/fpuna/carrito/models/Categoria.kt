package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// define una entidad de datos
@Entity
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val icono: String? = null // Puede ser null si no se define un ícono
)

