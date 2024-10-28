package com.fpuna.carrito.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fpuna.carrito.models.Cliente

@Dao
interface ClienteDao {
    @Insert
    suspend fun insert(cliente: Cliente)

    @Query("SELECT * FROM cliente WHERE cedula = :cedula LIMIT 1")
    suspend fun getClienteByCedula(cedula: String): Cliente?
}
