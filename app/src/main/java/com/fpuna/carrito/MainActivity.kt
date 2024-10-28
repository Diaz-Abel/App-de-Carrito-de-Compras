package com.fpuna.carrito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.fpuna.carrito.navegation.NavManager
import com.fpuna.carrito.ui.theme.CarritoTheme
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarritoTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    val dataBase = Room.databaseBuilder(this, AppDatabase::class.java, "db_carrito").build()
                    val categoriaDao = dataBase.categoriaDao()
                    val productoDao = dataBase.productoDao() // Asegúrate de tener este DAO implementado

                    val categoriaViewModel = CategoriaViewModel(categoriaDao)
                    val productoViewModel = ProductoViewModel(productoDao) // Instancia del ProductoViewModel

                    NavManager(categoriaViewModel, productoViewModel) // Pasa ambos ViewModels
                }

            }
        }
    }
}


