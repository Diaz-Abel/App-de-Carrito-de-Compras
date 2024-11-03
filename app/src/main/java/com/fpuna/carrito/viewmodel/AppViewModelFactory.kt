package com.fpuna.carrito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fpuna.carrito.daos.CarritoDao
import com.fpuna.carrito.daos.CategoriaDao
import com.fpuna.carrito.daos.ClienteDao
import com.fpuna.carrito.daos.ProductoDao
import com.fpuna.carrito.daos.VentaDao
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.viewmodel.VentaViewModel
import com.fpuna.carrito.viewmodel.CarritoViewModel


// Esta clase permite crear instancias de diferentes ViewModels de manera dinámica.
class AppViewModelFactory(
    private val categoriaDao: CategoriaDao,
    private val productoDao: ProductoDao,
    private val ventaDao: VentaDao,
    private val clienteDao: ClienteDao,
    private val carritoDao: CarritoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CategoriaViewModel::class.java) -> {
                CategoriaViewModel(categoriaDao) as T
            }

            modelClass.isAssignableFrom(ProductoViewModel::class.java) -> {
                ProductoViewModel(productoDao) as T
            }

            modelClass.isAssignableFrom(VentaViewModel::class.java) -> {
                VentaViewModel(ventaDao, clienteDao) as T
            }

            modelClass.isAssignableFrom(CarritoViewModel::class.java) -> {
                CarritoViewModel(carritoDao) as T
            }

            else -> throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}