package com.fpuna.carrito.utils

import com.fpuna.carrito.models.Categoria

// Función para validar campos obligatorios
fun validarCamposObligatorios(
    nombre: String,
    precioVenta: String,
    cantidadDisponible: String,
    selectedCategoria: Categoria?
): String? {
    return when {
        nombre.isEmpty() -> "El nombre no puede estar vacío."
        precioVenta.isEmpty() -> "El precio no puede estar vacío."
        cantidadDisponible.isEmpty() -> "La cantidad no puede estar vacía."
        selectedCategoria == null -> "Debe seleccionar una categoría."
        else -> null
    }
}

// Función para validar precios y cantidades
fun validarValoresNumericos(precioVenta: String, cantidadDisponible: String): String? {
    val precio = precioVenta.toDoubleOrNull()
    val cantidad = cantidadDisponible.toDoubleOrNull()

    return when {
        precio == null || precio <= 0 -> "El precio debe ser un número válido mayor que cero."
        cantidad == null || cantidad <= 0 -> "La cantidad debe ser un número válido mayor que cero."
        cantidad % 1 != 0.0 -> "La cantidad debe ser un número entero." // Verifica si es entero
        else -> null
    }
}