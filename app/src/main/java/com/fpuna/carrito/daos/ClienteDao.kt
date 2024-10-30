package com.fpuna.carrito.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fpuna.carrito.models.Cliente

@Dao
interface ClienteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: Cliente)

    @Query("SELECT * FROM clientes WHERE cedula = :cedula LIMIT 1")
    suspend fun getClienteByCedula(cedula: String): Cliente?
}
