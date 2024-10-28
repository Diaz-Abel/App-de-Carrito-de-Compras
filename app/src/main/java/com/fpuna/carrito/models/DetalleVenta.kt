package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalle_venta")
data class DetalleVenta(
    @PrimaryKey(autoGenerate = true) val idDetalleVenta: Long = 0,
    val idVenta: Long? = null, // Asegúrate de que sea opcional
    val idProducto: Long,
    val cantidad: Int,
    val precio: Double
)
