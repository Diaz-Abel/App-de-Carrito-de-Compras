package com.fpuna.carrito

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.models.DetalleVenta
import com.fpuna.carrito.daos.CategoriaDao
import com.fpuna.carrito.daos.ClienteDao
import com.fpuna.carrito.daos.ProductoDao
import com.fpuna.carrito.daos.VentaDao
import com.fpuna.carrito.daos.DetalleVentaDao
import com.fpuna.carrito.models.Cliente

@Database(
    entities = [Categoria::class, Producto::class, Venta::class, DetalleVenta::class, Cliente::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao // Agregar el DAO de Venta
    abstract fun detalleVentaDao(): DetalleVentaDao // Agregar el DAO de DetalleVenta
    abstract fun clienteDao(): ClienteDao // Agrega esta línea

}
