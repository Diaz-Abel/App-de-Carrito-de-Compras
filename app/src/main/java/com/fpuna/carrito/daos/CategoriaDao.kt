package com.fpuna.carrito.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.fpuna.carrito.models.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM Categoria")
    fun getAll(): Flow<List<Categoria>>

    @Query("SELECT * FROM Categoria WHERE id = :id")
    suspend fun getById(id: Int): Categoria?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria)
    
    @Update
    suspend fun update(categoria: Categoria)

    @Delete
    suspend fun delete(categoria: Categoria)
}
