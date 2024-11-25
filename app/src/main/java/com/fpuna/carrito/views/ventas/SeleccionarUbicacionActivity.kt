package com.fpuna.carrito.views.ventas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.osmdroid.config.Configuration
import android.content.Context
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class SeleccionarUbicacionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

        // Configurar el User Agent String
        Configuration.getInstance().userAgentValue = "com.fpuna.carrito/1.0"

        setContent {
            // Si no usas navegación, puedes omitir el navController
            MapaSeleccionarUbicacion(navController = null) // Pasar null explícitamente si el diseño lo permite
        }
    }
}

@Composable
fun MapaSeleccionarUbicacion(navController: NavController?) { // Cambia el parámetro a tipo nullable
    val geoPointSeleccionado = remember { mutableStateOf<GeoPoint?>(null) }
    val currentLocation = remember { mutableStateOf<Location?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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

                val marker = Marker(mapView)
                marker.position = GeoPoint(-25.2637, -57.5759)
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
