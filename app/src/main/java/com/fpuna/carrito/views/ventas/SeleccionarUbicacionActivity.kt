package com.fpuna.carrito.views.ventas

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class SeleccionarUbicacionActivity : ComponentActivity() {

    // Registrar solicitud de permisos de ubicación
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = "com.fpuna.carrito/1.0"

        // Solicitar permisos de ubicación si no están otorgados
        requestLocationPermission()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setContent {
            MapaSeleccionarUbicacion(navController = null) // Pasar null explícitamente si el diseño lo permite
        }
    }

    // Función para verificar y solicitar permisos de ubicación
    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Método para obtener la ubicación actual utilizando LocationManager
    private fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                callback(location)
            } else {
                // Si no se obtiene la ubicación, se puede intentar con otro proveedor como NETWORK_PROVIDER
                val locationNetwork =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                callback(locationNetwork)
            }
        }
    }
}

@Composable
fun MapaSeleccionarUbicacion(navController: NavController?) {
    val geoPointSeleccionado = remember { mutableStateOf<GeoPoint?>(null) }
    val currentLocation = remember { mutableStateOf<Location?>(null) }

    // Solicitar la ubicación al inicio
    getCurrentLocation { location ->
        location?.let {
            currentLocation.value = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Selecciona la ubicación en el mapa", modifier = Modifier.padding(bottom = 8.dp))

        AndroidView(
            factory = { context ->
                val mapView = MapView(context)
                mapView.setMultiTouchControls(true) // Habilitar controles multitáctiles (zoom y arrastre)

                val mapController = mapView.controller
                mapController.setZoom(15.0)

                // Si la ubicación está disponible, usa esa como centro del mapa
                val initialGeoPoint = currentLocation.value?.let {
                    GeoPoint(it.latitude, it.longitude)
                } ?: GeoPoint(-25.2637, -57.5759) // Si no se obtiene ubicación, usar una fija.

                mapController.setCenter(initialGeoPoint)

                // Agregar marcador en la ubicación inicial
                val marker = Marker(mapView)
                marker.position = initialGeoPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(marker)

                // Mover el marcador al hacer clic en el mapa
                mapView.overlays.add(object : org.osmdroid.views.overlay.Overlay() {
                    override fun onSingleTapConfirmed(e: android.view.MotionEvent, mapView: MapView): Boolean {
                        val point = GeoPoint(
                            mapView.projection.fromPixels(e.x.toInt(), e.y.toInt())
                        )
                        geoPointSeleccionado.value = point
                        marker.position = point
                        mapView.invalidate()
                        return true
                    }
                })

                mapView
            },
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {
                if (geoPointSeleccionado.value != null) {
                    // Verifica si el navController es nulo antes de usarlo
                    navController?.previousBackStackEntry?.savedStateHandle?.set(
                        "geoPointSeleccionado",
                        geoPointSeleccionado.value?.let { Pair(it.latitude, it.longitude) }
                    )
                    // Navegar hacia atrás solo si hay un navController
                    navController?.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Aceptar Ubicación")
        }
    }
}

// Obtiene la ubicación actual (debe ser llamada desde un Composable o Activity)
@Composable
fun getCurrentLocation(callback: (Location?) -> Unit) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
    ) {
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        callback(location)
    }
}
