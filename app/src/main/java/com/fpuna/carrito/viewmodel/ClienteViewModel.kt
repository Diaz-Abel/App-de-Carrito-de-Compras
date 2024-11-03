package com.fpuna.carrito.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpuna.carrito.daos.ClienteDao
import com.fpuna.carrito.models.Cliente
import com.fpuna.carrito.states.ClienteState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ClienteViewModel(private val dao: ClienteDao) : ViewModel() {
    var state by mutableStateOf(ClienteState())
        private set

    var uiState by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            dao.getAll().collectLatest {
                state = state.copy(listaClientes = it)
            }
        }
    }

    fun agregarCliente(cliente: Cliente) = viewModelScope.launch {
        try {
            dao.insert(cliente)
            uiState = "Cliente agregado exitosamente"
        } catch (e: Exception) {
            uiState = "Error al agregar Cliente"
        }
    }

    fun actualizarCliente(cliente: Cliente) = viewModelScope.launch {
        try {
            dao.update(cliente)
            uiState = "Cliente actualizado exitosamente"
        } catch (e: Exception) {
            uiState = "Error al actualizar Cliente"
        }
    }

    fun borrarCliente(cliente: Cliente) = viewModelScope.launch {
        try {
            dao.delete(cliente)
            uiState = "Cliente eliminada exitosamente"
        } catch (e: SQLiteConstraintException) {
            uiState =
                "No se puede eliminar este cliente porque está relacionada con otros registros."
        }
    }

    fun getCliente(cedula: String) = viewModelScope.launch {
        try {
            val cliente = dao.getClienteByCedula(cedula)
            state = state.copy(cliente = cliente) // Actualizamos el estado con el cliente obtenido
            uiState = "Cliente encontrado"
        } catch (e: Exception) {
            uiState = "Error al obtener el cliente"
        }
    }
}