package com.fpuna.carrito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.fpuna.carrito.navegation.NavManager
import com.fpuna.carrito.ui.theme.CarritoTheme
import com.fpuna.carrito.viewmodel.AppViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Instancia de la base de datos y daos necesarios
        val dataBase =
            Room.databaseBuilder(this, AppDatabase::class.java, "db_carrito").build()
        val categoriaDao = dataBase.categoriaDao()
        val productoDao = dataBase.productoDao()
        val ventaDao = dataBase.ventaDao()
        val clienteDao = dataBase.clienteDao()
        val carritoDao = dataBase.carritoDao()

        // Crea el ViewModelFactory con daos necesarios
        val viewModelFactory =
            AppViewModelFactory(categoriaDao, productoDao, ventaDao, clienteDao, carritoDao)

        // sirve para configurar el contenido de la pantalla
        setContent {
            CarritoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MenuLateral(viewModelFactory)

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuLateral(viewModelFactory: AppViewModelFactory) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    // Estado para el título
    var titulo by remember { mutableStateOf("Inicio") }

    // Observa la ruta actual del NavController
    val backStackEntry = navController.currentBackStackEntryAsState()

    // Cambia el título basado en la ruta actual
    LaunchedEffect(backStackEntry.value?.destination?.route) {
        when (backStackEntry.value?.destination?.route) {
            "carrito" -> titulo = "Carrito"
            "inicioVenta" -> titulo = "Inicio"
            "inicioCategoria" -> titulo = "Categorías"
            "agregarCategoria" -> titulo = "Agregar categoría"
            "editarCategoria/{id}/{name}" -> titulo = "Editar categoría"
            "inicioProducto" -> titulo = "Productos"
            "agregarProducto" -> titulo = "Agregar producto"
            "editarProducto/{id}/{nombre}" -> titulo = "Editar producto"
            "inicioCliente" -> titulo = "Clientes"
            "agregarCliente" -> titulo = "Agregar cliente"
            "editarCliente/{cedula}" -> titulo = "Editar cliente"
            "consultaVentas" -> titulo = "Consultar Ventas"
            // Agrega más rutas según sea necesario
            else -> titulo = "Inicio" // Valor por defecto o el que desees
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "Inicio") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("inicioVenta")
                    }
                )
                HorizontalDivider()

                // Opción de menú para ir a "Categorias"
                NavigationDrawerItem(
                    label = { Text(text = "Categorias") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("inicioCategoria")
                    },
                )
                HorizontalDivider()

                // Opción de menú para ir a "Productos"
                NavigationDrawerItem(
                    label = { Text(text = "Productos") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("inicioProducto")
                    },
                )
                HorizontalDivider()
                // Nueva opción de menú para Clientes
                NavigationDrawerItem(
                    label = { Text(text = "Clientes") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("inicioCliente")
                    }
                )
                HorizontalDivider()

                // Nueva opción de menú para "Consultar Ventas"
                NavigationDrawerItem(
                    label = { Text(text = "Consultar Ventas") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("consultaVentas")
                    },
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            titulo,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = { coroutineScope.launch { drawerState.open() } }
                        ) {
                            Image(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menu"
                            )
                        }
                    },
                    actions = {
                        // Agregar el ícono del carrito
                        IconButton(onClick = {
                            navController.navigate("carrito")
                        }) {
                            Image(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Carrito de compras",
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }

                    }
                )
            }) { paddingValues ->
            NavManager(navController, viewModelFactory, modifier = Modifier.padding(paddingValues))
        }
    }
}


