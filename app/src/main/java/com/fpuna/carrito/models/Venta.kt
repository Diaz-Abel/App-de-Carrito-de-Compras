package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "venta")
data class Venta(
    @PrimaryKey(autoGenerate = true) val idVenta: Long = 0,
    val fecha: String,
    val idCliente: Int,
    val total: Double
)
