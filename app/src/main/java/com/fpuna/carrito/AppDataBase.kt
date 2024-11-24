package com.fpuna.carrito

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fpuna.carrito.daos.*
import com.fpuna.carrito.models.*

@Database(
    entities = [Categoria::class, Producto::class, Venta::class, Cliente::class, DetalleVenta::class, CarritoItem::class],
    version = 2, // Cambia la versión
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao
    abstract fun clienteDao(): ClienteDao
    abstract fun carritoDao(): CarritoDao
}

