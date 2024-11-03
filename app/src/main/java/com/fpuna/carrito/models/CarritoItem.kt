package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "carrito",
    indices = [Index(value = ["idProducto"])], // Añadimos un índice para mejorar el rendimiento en idProducto
    foreignKeys = [ForeignKey(
        entity = Producto::class,
        parentColumns = ["idProducto"],
        childColumns = ["idProducto"],
        onDelete = ForeignKey.NO_ACTION // No elimina el CarritoItem si el Producto es eliminado
    )]
)
data class CarritoItem(
    @PrimaryKey(autoGenerate = true) val idCarritoItem: Long = 0,
    val idProducto: Int, // Clave foránea que hace referencia a Producto
    val cantidad: Int
)
