package com.fpuna.carrito.views.ventas

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun VerMapaView(latitude: Double, longitude: Double) {
    AndroidView(
        factory = { context ->
            val mapView = MapView(context)
            mapView.setMultiTouchControls(true)

            val mapController = mapView.controller
            mapController.setZoom(15.0)
            mapController.setCenter(GeoPoint(latitude, longitude))

            val marker = Marker(mapView)
            marker.position = GeoPoint(latitude, longitude)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)

            mapView
        },
        modifier = Modifier.fillMaxSize()
    )
}
