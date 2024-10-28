package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cliente")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val idCliente: Int = 0,
    val cedula: String,
    val nombre: String,
    val apellido: String
)
