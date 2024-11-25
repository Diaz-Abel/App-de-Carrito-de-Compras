package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "ventas",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["idCliente"],
            childColumns = ["idCliente"],
            onDelete = ForeignKey.NO_ACTION // No elimina las ventas asociadas si se elimina un cliente
        )
    ],
    indices = [Index(value = ["idCliente"])] // Añadimos un índice para idCliente
)
data class Venta(
    @PrimaryKey(autoGenerate = true) val idVenta: Long = 0,
    val fecha: String,
    val idCliente: Long, // Cambiado a Long para coincidir con Cliente
    val total: Double,
    val tipoOperacion: String, // "pickup" o "delivery"
    val direccionEntrega: String? = null, // Solo para delivery

)
