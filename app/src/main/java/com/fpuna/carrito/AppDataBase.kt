package com.fpuna.carrito

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.daos.CategoriaDao
import com.fpuna.carrito.daos.ProductoDao

@Database(entities = [Categoria::class, Producto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao
}
