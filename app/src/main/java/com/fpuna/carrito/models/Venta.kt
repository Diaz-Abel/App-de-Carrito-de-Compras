package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ventas",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["idCliente"],
            childColumns = ["idCliente"],
            onDelete = ForeignKey.NO_ACTION // No elimina las ventas asociadas si se elimina un cliente
        )
    ]
)
data class Venta(
    @PrimaryKey(autoGenerate = true) val idVenta: Long = 0,
    val fecha: String,
    val idCliente: String,
    val total: Double
)
