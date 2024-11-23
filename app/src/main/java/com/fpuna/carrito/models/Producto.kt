package com.fpuna.carrito.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "productos",
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"], // Coincide con el campo en Categoria
        childColumns = ["idCategoria"],
        onDelete = ForeignKey.NO_ACTION
    )],
    indices = [Index(value = ["idCategoria"])]
)
data class Producto(
    @PrimaryKey(autoGenerate = true) val idProducto: Int = 0,
    val nombre: String,
    val idCategoria: Int, // Referencia a la categoría
    val precioVenta: Double,
    val imageUri: String? = null,
    var cantidadDisponible: Int
)


