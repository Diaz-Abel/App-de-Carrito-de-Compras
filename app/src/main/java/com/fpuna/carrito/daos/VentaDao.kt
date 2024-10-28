package com.fpuna.carrito.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fpuna.carrito.models.Venta

@Dao
interface VentaDao {
    @Insert
    suspend fun insert(venta: Venta): Long
}
