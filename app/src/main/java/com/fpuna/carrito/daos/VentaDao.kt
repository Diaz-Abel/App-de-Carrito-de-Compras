package com.fpuna.carrito.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fpuna.carrito.models.Venta
import com.fpuna.carrito.models.DetalleVentaProducto
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.models.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenta(venta: Venta)

    @Query("SELECT * FROM ventas")
    suspend fun getAllVentas(): List<Venta>

    @Query("SELECT * FROM ventas WHERE fecha = :fecha")
    suspend fun getVentasByFecha(fecha: String): List<Venta>

    @Query("SELECT * FROM ventas WHERE idCliente = :idCliente")
    suspend fun getVentasByCliente(idCliente: Long): List<Venta>

    @Query("""
    SELECT productos.nombre, detalle_ventas.cantidad, detalle_ventas.precio 
    FROM detalle_ventas 
    JOIN productos ON productos.idProducto = detalle_ventas.idProducto 
    WHERE detalle_ventas.idVenta = :idVenta
    """)
    fun getDetallesDeVenta(idVenta: Long): Flow<List<DetalleVentaProducto>>

}
