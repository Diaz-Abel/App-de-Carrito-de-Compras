package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val idCliente: Long = 0,
    val cedula: String,
    val nombre: String,
    val apellido: String
)
