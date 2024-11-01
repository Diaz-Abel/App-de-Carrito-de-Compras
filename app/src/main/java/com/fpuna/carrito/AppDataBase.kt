package com.fpuna.carrito

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fpuna.carrito.daos.CarritoDao
import com.fpuna.carrito.daos.CategoriaDao
import com.fpuna.carrito.daos.ClienteDao
import com.fpuna.carrito.daos.ProductoDao
import com.fpuna.carrito.daos.VentaDao
import com.fpuna.carrito.models.CarritoItem
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.DetalleVenta
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.models.DetalleVentaProducto


@Database(
    entities = [Categoria::class, Producto::class, Venta::class, Cliente::class, DetalleVenta::class, CarritoItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao
    abstract fun clienteDao(): ClienteDao
    abstract fun carritoDao(): CarritoDao
}
