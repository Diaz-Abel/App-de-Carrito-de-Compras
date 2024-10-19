package com.fpuna.carrito.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fpuna.carrito.models.Categoria
import kotlinx.coroutines.flow.Flow

// Aquí se definen los métodos para interactuar con los datos
@Dao
interface CategoriaDao {
    /*
    * Las funciónes se declaran como suspend para realizar la operación en un hilo en segundo plano,
    * para evitar bloquear el hilo principal
    * */

    // Insertar una categoría nueva
    @Insert
    suspend fun insert(categoria: Categoria)

    // Actualizar una categoría
    @Update
    suspend fun update(categoria: Categoria)

    // Eliminar una categoría
    @Delete
    suspend fun delete(categoria: Categoria)

    /*
    * Un Flow permite a los consumidores (suscriptores) recibir actualizaciones de datos
    *  a medida que estén disponibles, sin bloquear el hilo.
    * */
    // Retornar todas las categorías
    @Query("SELECT * FROM Categoria")
    fun getAll(): Flow<List<Categoria>>

    // Retornar una categoría específica
    @Query("SELECT * FROM Categoria WHERE id = :id")
    fun getById(id: Int): Flow<Categoria>

    // Buscar por nombre de categoría
    @Query("SELECT * FROM Categoria WHERE name LIKE :name")
    fun findByName(name: String): Flow<Categoria>
}