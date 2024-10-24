import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.models.Producto
import com.fpuna.carrito.viewmodel.CategoriaViewModel
import com.fpuna.carrito.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoView(
    navController: NavController,
    productoViewModel: ProductoViewModel,
    categoriaViewModel: CategoriaViewModel, // Pasamos el CategoriaViewModel para obtener categorías
    id: Int,
    nombreProducto: String
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Editar Producto",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                }
            )
        }) {
        ContentEditarProductoView(it, navController, productoViewModel, categoriaViewModel, id, nombreProducto)
    }
}

@Composable
fun ContentEditarProductoView(
    paddingValues: PaddingValues,
    navController: NavController,
    productoViewModel: ProductoViewModel,
    categoriaViewModel: CategoriaViewModel, // Pasamos el viewModel de categorías
    id: Int,
    nombreProducto: String
) {
    var nombre by remember { mutableStateOf(nombreProducto) }
    var precioVenta by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) } // Categoria seleccionada
    var expanded by remember { mutableStateOf(false) } // Controlar el menú desplegable

    // Obtener las categorías desde el CategoriaViewModel
    val listaCategorias = categoriaViewModel.state.listaCategorias

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 30.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(text = "Nombre del Producto") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )

        OutlinedTextField(
            value = precioVenta,
            onValueChange = { precioVenta = it },
            label = { Text(text = "Precio de Venta") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )

        // Dropdown para seleccionar la categoría
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategoria?.name ?: "Selecciona una categoría",
                onValueChange = {},
                label = { Text(text = "Categoría") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listaCategorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.name) },
                        onClick = {
                            selectedCategoria = categoria
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (selectedCategoria != null) {
                    val producto = Producto(
                        idProducto = id,
                        nombre = nombre,
                        precioVenta = precioVenta.toDouble(),
                        idCategoria = selectedCategoria!!.id // Asigna el ID de la categoría seleccionada
                    )
                    productoViewModel.actualizarProducto(producto)
                    navController.popBackStack()
                }
            }
        ) {
            Text(text = "Guardar Cambios")
        }
    }
}
