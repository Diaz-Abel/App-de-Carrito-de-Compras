package com.fpuna.carrito.navegation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fpuna.carrito.viewmodel.AppViewModelFactory
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.views.categoria.AgregarCategoriaView
import com.fpuna.carrito.views.categoria.EditarCategoriaView
import com.fpuna.carrito.views.categoria.InicioCategoriaView
import com.fpuna.carrito.views.producto.AgregarProductoView
import com.fpuna.carrito.views.producto.EditarProductoView
import com.fpuna.carrito.views.producto.ListarProductosView

@Composable
fun NavManager(
    navController: NavHostController,
    viewModelFactory: AppViewModelFactory,
    modifier: Modifier
) {
    // Instancia de ViewModels que se necesita
    val categoriaViewModel: CategoriaViewModel = viewModel(factory = viewModelFactory)
    val productoViewModel: ProductoViewModel = viewModel(factory = viewModelFactory)
    NavHost(navController = navController, startDestination = "inicio", modifier = modifier) {
        composable(route = "inicio") {
            // Fondo blanco temporal
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = androidx.compose.ui.graphics.Color.White
            ) {
                // Text("Inicio - Fondo Blanco Temporal")
            }

        }
        composable(route = "inicioCategoria") {
            InicioCategoriaView(navController, categoriaViewModel)
        }
        composable(route = "agregarCategoria") {
            AgregarCategoriaView(navController, categoriaViewModel)
        }
        composable(
            route = "editarCategoria/{id}/{name}",
            arguments = listOf(
                navArgument(name = "id") { type = NavType.IntType },
                navArgument(name = "name") {
                    type = NavType.StringType; defaultValue = "Sin Especificar"
                }
            )
        ) {
            EditarCategoriaView(
                navController,
                categoriaViewModel,
                it.arguments!!.getInt("id"),
                it.arguments!!.getString("name")!!
            )
        }

        // Navegación para productos
        composable(route = "inicioProducto") {
            val categorias = categoriaViewModel.state.listaCategorias
            ListarProductosView(navController, productoViewModel, categorias)
        }
        composable(route = "agregarProducto") { backStackEntry ->
            val categorias =
                categoriaViewModel.state.listaCategorias // Obtener las categorías del ViewModel
            AgregarProductoView(navController, productoViewModel, categorias)
        }
        composable(
            route = "editarProducto/{id}/{nombre}",
            arguments = listOf(
                navArgument(name = "id") { type = NavType.IntType },
                navArgument(name = "nombre") {
                    type = NavType.StringType; defaultValue = "Sin Especificar"
                }
            )
        ) {
            val id = it.arguments!!.getInt("id")
            val nombre = it.arguments!!.getString("nombre")!!

            val producto = productoViewModel.state.listaProductos.find { it.idProducto == id }

            if (producto != null) {
                EditarProductoView(
                    navController = navController,
                    productoViewModel = productoViewModel,
                    categoriaViewModel = categoriaViewModel,
                    producto = producto
                )
            } else {
                Text("Producto no encontrado")
            }
        }
    }
}