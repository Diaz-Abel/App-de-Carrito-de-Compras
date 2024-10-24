package com.fpuna.carrito

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.daos.CategoriaDao


/**
 * Se define una clase AppDataBase para contener la base de datos
 * Para cada clase DAO que se asoció con la base de datos, esta base de datos debe
 * definir un método abstracto que tenga cero argumentos y muestre una instancia de
 * la clase DAO.
 */
@Database(entities = [Categoria::class, Producto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao
}
