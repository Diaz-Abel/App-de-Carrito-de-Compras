package com.fpuna.carrito

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.fpuna.carrito.navegation.NavManager
import com.fpuna.carrito.ui.theme.CarritoTheme
import com.fpuna.carrito.viewmodel.AppViewModelFactory
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    // Registrar el Activity Result API para solicitar permisos múltiples
    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { (permission, isGranted) ->
                if (!isGranted) {
                    // Manejar permisos denegados si es necesario
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configuración de OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = "com.fpuna.carrito/1.0"

        // Solicitar permisos necesarios
        requestPermissionsIfNeeded()

        //deleteDatabase("db_carrito")

        // Configurar la base de datos
        val dataBase =
            Room.databaseBuilder(this, AppDatabase::class.java, "db_carrito")
                .fallbackToDestructiveMigration()
                .build()
        val categoriaDao = dataBase.categoriaDao()
        val productoDao = dataBase.productoDao()
        val ventaDao = dataBase.ventaDao()
        val clienteDao = dataBase.clienteDao()
        val carritoDao = dataBase.carritoDao()

        val viewModelFactory =
            AppViewModelFactory(categoriaDao, productoDao, ventaDao, clienteDao, carritoDao)

        // Configurar el contenido de la pantalla
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

    private fun requestPermissionsIfNeeded() {
        val permissions = listOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGrantedPermissions.isNotEmpty()) {
            requestMultiplePermissionsLauncher.launch(notGrantedPermissions.toTypedArray())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuLateral(viewModelFactory: AppViewModelFactory) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    var titulo by remember { mutableStateOf("Inicio") }

    val backStackEntry = navController.currentBackStackEntryAsState()

    LaunchedEffect(backStackEntry.value?.destination?.route) {
        titulo = when (backStackEntry.value?.destination?.route) {
            "carrito" -> "Carrito"
            "inicioVenta" -> "Inicio"
            "inicioCategoria" -> "Categorías"
            "agregarCategoria" -> "Agregar categoría"
            "editarCategoria/{id}/{name}" -> "Editar categoría"
            "inicioProducto" -> "Productos"
            "agregarProducto" -> "Agregar producto"
            "editarProducto/{id}/{nombre}" -> "Editar producto"
            "inicioCliente" -> "Clientes"
            "agregarCliente" -> "Agregar cliente"
            "editarCliente/{cedula}" -> "Editar cliente"
            "consultaVentas" -> "Consultar Ventas"
            else -> "Inicio"
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
                NavigationDrawerItem(
                    label = { Text(text = "Categorias") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("inicioCategoria")
                    },
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Productos") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("inicioProducto")
                    },
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Clientes") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("inicioCliente")
                    }
                )
                HorizontalDivider()
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
                            color = Color.White,
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
            }
        ) { paddingValues ->
            NavManager(navController, viewModelFactory, modifier = Modifier.padding(paddingValues))
        }
    }
}
