package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "detalle_ventas",
    foreignKeys = [
        ForeignKey(
            entity = Venta::class,
            parentColumns = ["idVenta"],
            childColumns = ["idVenta"],
            onDelete = ForeignKey.NO_ACTION // No eliminar los detalles al eliminar una venta
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["idProducto"],
            childColumns = ["idProducto"],
            onDelete = ForeignKey.NO_ACTION // No eliminar los detalles al eliminar un producto
        )
    ],
    indices = [Index(value = ["idVenta"]), Index(value = ["idProducto"])]
)
data class DetalleVenta(
    @PrimaryKey(autoGenerate = true) val idDetalleVenta: Long = 0,
    val idVenta: Long,
    val idProducto: Long,
    val cantidad: Int,
    val precio: Double
)
