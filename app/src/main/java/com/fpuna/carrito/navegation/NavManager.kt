package com.fpuna.carrito.navegation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.views.AgregarView
import com.fpuna.carrito.views.EditarView
import com.fpuna.carrito.views.InicioView

@Composable
fun NavManager(viewModel: CategoriaViewModel) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "inicio") {
        composable(route = "inicio") {
            InicioView(navController, viewModel)
        }
        composable(route = "agregar") {
            AgregarView(navController, viewModel)
        }
        composable(
            route = "editar/{id}/{name}",
            arguments = listOf(
                navArgument(name = "id") { type = NavType.IntType },
                navArgument(name = "name") { type = NavType.StringType; defaultValue = "Sin Especificar" })
        ) {
            EditarView(
                navController,
                viewModel,
                it.arguments!!.getInt("id"),
                it.arguments!!.getString("name")!! // Forzar no nulo
            )
        }


    }
}