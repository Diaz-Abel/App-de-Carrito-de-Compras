package com.fpuna.carrito.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fpuna.carrito.models.CarritoItem
import com.fpuna.carrito.models.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarritoItem(carritoItem: CarritoItem)

    @Query("SELECT * FROM carrito")
    suspend fun getAllCarritoItems(): List<CarritoItem>

    @Query("DELETE FROM carrito WHERE idCarritoItem = :idCarritoItem")
    suspend fun deleteCarritoItem(idCarritoItem: Long)

    @Query("DELETE FROM carrito")
    suspend fun clearCarrito()

    @Query("SELECT * FROM productos WHERE idProducto=:id")
    suspend fun getProductosById(id: Int): Producto

    @Query("SELECT * FROM carrito WHERE idProducto = :idProducto LIMIT 1")
    suspend fun getCarritoItemByIdProducto(idProducto: Int): CarritoItem?

    @Query("SELECT * FROM carrito")
    fun getItemsFlow(): Flow<List<CarritoItem>>

    // Método para obtener un item del carrito por ID
    @Query("SELECT * FROM carrito WHERE idCarritoItem = :idCarritoItem LIMIT 1")
    suspend fun getCarritoItemById(idCarritoItem: Long): CarritoItem?
}
