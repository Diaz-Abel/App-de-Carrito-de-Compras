package com.fpuna.carrito.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fpuna.carrito.models.Producto

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(producto: Producto)

    @Update
    suspend fun update(producto: Producto)

    @Query("DELETE FROM productos WHERE idProducto = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProductos(): LiveData<List<Producto>>

    @Query("SELECT * FROM productos WHERE nombre LIKE :nombre ORDER BY nombre ASC")
    fun getProductosByName(nombre: String): LiveData<List<Producto>>

    @Query("SELECT * FROM productos WHERE idCategoria = :idCategoria ORDER BY nombre ASC")
    fun getProductosByCategoria(idCategoria: Int): LiveData<List<Producto>>
}
