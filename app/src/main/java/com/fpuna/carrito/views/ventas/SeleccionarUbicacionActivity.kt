package com.fpuna.carrito.views.ventas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.tooling.preview.Preview
import org.osmdroid.util.GeoPoint

class SeleccionarUbicacionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar el cache de OSM
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

        setContent {
            MapaSeleccionarUbicacion()
        }
    }
}

@Composable
fun MapaSeleccionarUbicacion() {
    AndroidView(
        factory = { context ->
            val mapView = MapView(context)
            mapView.setMultiTouchControls(true)

            // Configura el mapa
            val mapController = mapView.controller
            mapController.setZoom(15.0)
            mapController.setCenter(GeoPoint(-25.2637, -57.5759)) // Ubicación inicial: Asunción

            // Agrega un marcador
            val marker = Marker(mapView)
            marker.position = GeoPoint(-25.2637, -57.5759)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "Ubicación seleccionada"
            mapView.overlays.add(marker)

            // Habilita clic en el mapa para cambiar el marcador
            mapView.setOnClickListener { v ->
                val geoPoint = mapView.mapCenter as GeoPoint
                marker.position = geoPoint
                mapView.invalidate()
            }

            mapView
        },
        modifier = Modifier.fillMaxSize()
    )
}
