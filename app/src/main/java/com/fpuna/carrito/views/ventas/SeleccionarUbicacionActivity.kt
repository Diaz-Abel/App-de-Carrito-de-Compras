package com.fpuna.carrito.views.ventas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.osmdroid.config.Configuration
import android.content.Context
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
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class SeleccionarUbicacionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar OSMDroid
        Configuration.getInstance().load(this, this.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

        setContent {
            MapaSeleccionarUbicacion(navController = null) // O ajusta el NavController según tu lógica
        }
    }
}

@Composable
fun MapaSeleccionarUbicacion(navController: NavController?) {
    val geoPointSeleccionado = remember { mutableStateOf<GeoPoint?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Selecciona la ubicación en el mapa", modifier = Modifier.padding(bottom = 8.dp))

        AndroidView(
            factory = { context ->
                val mapView = MapView(context)
                mapView.setMultiTouchControls(true)

                val mapController = mapView.controller
                mapController.setZoom(15.0)
                mapController.setCenter(GeoPoint(-25.2637, -57.5759)) // Ubicación inicial

                val marker = Marker(mapView)
                marker.position = GeoPoint(-25.2637, -57.5759)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(marker)

                mapView.setOnTouchListener { _, event ->
                    if (event.action == android.view.MotionEvent.ACTION_UP) {
                        val point = GeoPoint(
                            mapView.projection.fromPixels(event.x.toInt(), event.y.toInt())
                        )
                        geoPointSeleccionado.value = point
                        marker.position = point
                        mapView.invalidate()
                    }
                    true
                }

                mapView
            },
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {
                if (geoPointSeleccionado.value != null) {
                    // Guardar las coordenadas seleccionadas en el NavController
                    navController?.previousBackStackEntry?.savedStateHandle?.set(
                        "geoPointSeleccionado",
                        geoPointSeleccionado.value?.let { Pair(it.latitude, it.longitude) }
                    )
                    navController?.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Aceptar Ubicación")
        }
    }
}
