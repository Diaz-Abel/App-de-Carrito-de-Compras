package com.fpuna.carrito.viewmodel

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

    init {
        viewModelScope.launch {
            dao.getAll().collectLatest {
                state = state.copy(
                    // actualiza el estado de la lista de categorias
                    listaCategorias = it
                )
            }
        }
    }

    fun agregarCategoria(categoria: Categoria) = viewModelScope.launch {
        dao.insert(categoria = categoria)
    }

    fun actualizarCategoria(categoria: Categoria) = viewModelScope.launch {
        dao.update(categoria = categoria)
    }

    fun borrarCategoria(categoria: Categoria) = viewModelScope.launch {
        dao.delete(categoria = categoria)
    }
}