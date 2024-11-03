package com.fpuna.carrito.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.CategoriaDao
import com.fpuna.carrito.models.Categoria
import com.fpuna.carrito.states.CategoriaState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoriaViewModel(private val dao: CategoriaDao) : ViewModel() {

    var state by mutableStateOf(CategoriaState())
        private set

    var uiState by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            dao.getAll().collectLatest {
                state = state.copy(listaCategorias = it)
            }
        }
    }

    fun agregarCategoria(categoria: Categoria) = viewModelScope.launch {
        try {
            dao.insert(categoria)
            uiState = "Categoría agregada exitosamente"
        } catch (e: Exception) {
            uiState = "Error al agregar categoría"
        }
    }

    fun actualizarCategoria(categoria: Categoria) = viewModelScope.launch {
        try {
            dao.update(categoria)
            uiState = "Categoría actualizada exitosamente"
        } catch (e: Exception) {
            uiState = "Error al actualizar categoría"
        }
    }

    fun borrarCategoria(categoria: Categoria) = viewModelScope.launch {
        try {
            dao.delete(categoria)
            uiState = "Categoría eliminada exitosamente"
        } catch (e: SQLiteConstraintException) {
            uiState =
                "No se puede eliminar esta categoría porque está relacionada con otros registros."
        }
    }
}
