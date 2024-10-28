package com.fpuna.carrito.navegation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.views.AgregarView
import com.fpuna.carrito.views.EditarView
import com.fpuna.carrito.views.InicioView
import com.fpuna.carrito.views.AgregarProductoView
import com.fpuna.carrito.views.EditarProductoView
import com.fpuna.carrito.views.ListarProductosView

@Composable
fun NavManager(categoriaViewModel: CategoriaViewModel, productoViewModel: ProductoViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "inicio") {
        composable(route = "inicio") {
            InicioView(navController, categoriaViewModel)
        }
        composable(route = "agregar") {
            AgregarView(navController, categoriaViewModel)
        }
        composable(
            route = "editar/{id}/{name}",
            arguments = listOf(
                navArgument(name = "id") { type = NavType.IntType },
                navArgument(name = "name") { type = NavType.StringType; defaultValue = "Sin Especificar" }
            )
        ) {
            EditarView(
                navController,
                categoriaViewModel,
                it.arguments!!.getInt("id"),
                it.arguments!!.getString("name")!!
            )
        }

        // Navegación para productos
        composable(route = "listar") {
            val categorias = categoriaViewModel.state.listaCategorias
            ListarProductosView(navController, productoViewModel, categorias)
        }
        composable(route = "agregarProducto") { backStackEntry ->
            val categorias = categoriaViewModel.state.listaCategorias // Obtener las categorías del ViewModel
            AgregarProductoView(navController, productoViewModel, categorias)
        }
        composable(
            route = "editarProducto/{id}/{nombre}",
            arguments = listOf(
                navArgument(name = "id") { type = NavType.IntType },
                navArgument(name = "nombre") { type = NavType.StringType; defaultValue = "Sin Especificar" }
            )
        ) {
            val id = it.arguments!!.getInt("id")
            val nombre = it.arguments!!.getString("nombre")!!

            val producto = productoViewModel.state.listaProductos.find { it.idProducto == id }

            if (producto != null) {
                EditarProductoView(
                    paddingValues = PaddingValues(),
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