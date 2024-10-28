package com.fpuna.carrito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.fpuna.carrito.navegation.NavManager
import com.fpuna.carrito.ui.theme.CarritoTheme
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.viewmodel.VentaViewModel
import com.fpuna.carrito.viewmodel.DetalleVentaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarritoTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val database = Room.databaseBuilder(this, AppDatabase::class.java, "db_carrito").build()
                    val categoriaDao = database.categoriaDao()
                    val productoDao = database.productoDao() // Asegúrate de tener este DAO implementado
                    val ventaDao = database.ventaDao() // Agrega el DAO de Venta
                    val detalleVentaDao = database.detalleVentaDao() // Agrega el DAO de DetalleVenta
                    val clienteDao = database.clienteDao() // Asegúrate de tener este DAO implementado

                    // Instancias de los ViewModels
                    val categoriaViewModel = CategoriaViewModel(categoriaDao)
                    val productoViewModel = ProductoViewModel(productoDao)
                    val ventaViewModel = VentaViewModel(ventaDao, detalleVentaDao, clienteDao)
                    val detalleVentaViewModel = DetalleVentaViewModel(detalleVentaDao) // Instancia del DetalleVentaViewModel

                    // Pasa todos los ViewModels a NavManager
                    NavManager(categoriaViewModel, productoViewModel, ventaViewModel)
                }
            }
        }
    }
}
