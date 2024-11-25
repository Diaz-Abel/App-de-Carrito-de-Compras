package com.fpuna.carrito.navegation

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
import com.fpuna.carrito.viewmodel.CarritoViewModel
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ClienteViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel
import com.fpuna.carrito.viewmodel.VentaViewModel
import com.fpuna.carrito.views.categoria.AgregarCategoriaView
import com.fpuna.carrito.views.categoria.EditarCategoriaView
import com.fpuna.carrito.views.categoria.InicioCategoriaView
import com.fpuna.carrito.views.cliente.AgregarClienteView
import com.fpuna.carrito.views.cliente.EditarClienteView
import com.fpuna.carrito.views.cliente.InicioClienteView
import com.fpuna.carrito.views.producto.AgregarProductoView
import com.fpuna.carrito.views.producto.EditarProductoView
import com.fpuna.carrito.views.producto.ListarProductosView
import com.fpuna.carrito.views.ventas.CarritoView
import com.fpuna.carrito.views.ventas.ConsultaVentasView
import com.fpuna.carrito.views.ventas.DetalleVentaView
import com.fpuna.carrito.views.ventas.FinalizarOrdenView
import com.fpuna.carrito.views.ventas.ListarVentaProductos
import com.fpuna.carrito.views.ventas.MapaSeleccionarUbicacion



@Composable
fun NavManager(
    navController: NavHostController,
    viewModelFactory: AppViewModelFactory,
    modifier: Modifier
) {
    // Instancia de ViewModels que se necesita
    val categoriaViewModel: CategoriaViewModel = viewModel(factory = viewModelFactory)
    val productoViewModel: ProductoViewModel = viewModel(factory = viewModelFactory)
    val ventaViewModel: VentaViewModel = viewModel(factory = viewModelFactory)
    val carritoViewModel: CarritoViewModel = viewModel(factory = viewModelFactory)
    val clienteViewModel: ClienteViewModel = viewModel(factory = viewModelFactory)

    NavHost(navController = navController, startDestination = "inicioVenta", modifier = modifier) {

        // VENTAS
        composable("inicioVenta") {
            val categorias = categoriaViewModel.state.listaCategorias
            ListarVentaProductos(
                productoViewModel,
                carritoViewModel,
                categorias
            )
        }
        composable("carrito") {
            CarritoView(navController, carritoViewModel, productoViewModel)
        }

        composable(
            route = "finalizarOrden?total={total}",
            arguments = listOf(navArgument("total") { type = NavType.FloatType })
        ) { backStackEntry ->
            val total = backStackEntry.arguments?.getFloat("total")?.toDouble() ?: 0.0

            FinalizarOrdenView(
                navController = navController,
                ventaViewModel = ventaViewModel,
                total = total,
                carritoViewModel,
                productoViewModel
            )
        }


        // Ruta para seleccionar la ubicación en el mapa
        composable("seleccionarUbicacion") {
            MapaSeleccionarUbicacion(navController = navController)
        }




        composable("consultaVentas") {
            ConsultaVentasView(
                ventaViewModel = ventaViewModel,
                navController = navController
            )
        }

        composable(
            "detalleVenta/{idVenta}",
            arguments = listOf(navArgument("idVenta") { type = NavType.LongType })
        ) {
            val idVenta = it.arguments?.getLong("idVenta") ?: 0L
            DetalleVentaView(ventaViewModel, idVenta)
        }


        // CATEGORIAS
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

        // PRODUCTOS
        composable(route = "inicioProducto") {
            val categorias = categoriaViewModel.state.listaCategorias
            ListarProductosView(navController, productoViewModel, categorias)
        }
        composable(route = "agregarProducto") {
            val categorias = categoriaViewModel.state.listaCategorias
            AgregarProductoView(navController, productoViewModel, categorias)
        }
        composable(
            route = "editarProducto/{id}",
            arguments = listOf(
                navArgument(name = "id") { type = NavType.IntType },
            )
        ) {
            val id = it.arguments!!.getInt("id")
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

        // CLIENTE
        composable("inicioCliente") {
            InicioClienteView(navController, clienteViewModel)
        }
        composable(
            route = "editarCliente/{cedula}",
            arguments = listOf(
                navArgument(name = "cedula") { type = NavType.StringType },
            )
        ) {
            EditarClienteView(
                navController,
                clienteViewModel,
                it.arguments!!.getString("cedula")!!
            )
        }
        composable(route = "agregarCliente") {
            AgregarClienteView(navController, clienteViewModel)
        }
    }
}
