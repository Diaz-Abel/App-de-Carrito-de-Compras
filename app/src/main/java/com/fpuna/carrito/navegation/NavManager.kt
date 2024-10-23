package com.fpuna.carrito.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
            ListarProductosView(navController, productoViewModel)
        }
        composable(route = "agregarProducto") {
            AgregarProductoView(navController, productoViewModel)
        }
        composable(
            route = "editarProducto/{id}/{nombre}",
            arguments = listOf(
                navArgument(name = "id") { type = NavType.IntType },
                navArgument(name = "nombre") { type = NavType.StringType; defaultValue = "Sin Especificar" }
            )
        ) {
            EditarProductoView(
                navController,
                productoViewModel,
                it.arguments!!.getInt("id"),
                it.arguments!!.getString("nombre")!!
            )
        }
    }
}
