package com.fpuna.carrito.daos

import androidx.room.Dao
import androidx.room.Insert
import com.fpuna.carrito.models.DetalleVenta

@Dao
interface DetalleVentaDao {
    @Insert
    suspend fun insert(detalle: DetalleVenta)
}
