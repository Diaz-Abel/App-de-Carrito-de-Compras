package com.fpuna.carrito.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fpuna.carrito.models.Cliente
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Query("SELECT * FROM clientes")
    fun getAll(): Flow<List<Cliente>>

    @Query("SELECT * FROM clientes WHERE cedula = :cedula LIMIT 1")
    suspend fun getClienteByCedula(cedula: String): Cliente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cliente: Cliente)

    @Update
    suspend fun update(cliente: Cliente)

    @Delete
    suspend fun delete(cliente: Cliente)
}
