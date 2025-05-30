package com.example.movildev.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.ViewModel
import com.example.movildev.model.Usuario

class UsuarioViewModel : ViewModel() {

    // 1. LiveData que contiene un Array
    private val _usuarios = MutableLiveData<Array<Usuario>>(arrayOf())   // arranca con array vacío
    val usuarios: LiveData<Array<Usuario>> get() = _usuarios

    init {
        // 2. Inicializas con tu “semilla” de datos
        _usuarios.value = arrayOf(
            Usuario(1, "Juan Pérez", "Cédula",    "123456789", "01/01/1990",
                "juan.perez@example.com",  "Catalina Cañon", "3024778127"),
            Usuario(2, "María Gómez", "Pasaporte", "A9876543",  "15/05/1985",
                "maria.gomez@example.com", "Catalina Cañon", "3103305802")
        )
    }

    /** Devuelve el usuario con el ID dado o null si no existe */
    fun obtenerUsuarioPorId(id: Int): LiveData<Usuario?> =
        usuarios.map { array -> array.firstOrNull { it.id == id } }

    /** Elimina el usuario del array */
    fun eliminarUsuario(usuario: Usuario) {
        _usuarios.value = _usuarios.value
            ?.filter { it.id != usuario.id }
            ?.toTypedArray()                           // ← vuelve a Array
    }

    /** Añade un usuario */
    fun agregarUsuario(usuario: Usuario) {
        val actuales = _usuarios.value ?: arrayOf()
        _usuarios.value = actuales + usuario           // `+` crea un nuevo Array
    }

    /** Reemplaza el usuario con el mismo ID */
    fun actualizarUsuario(usuario: Usuario) {
        _usuarios.value = _usuarios.value
            ?.map { if (it.id == usuario.id) usuario else it }
            ?.toTypedArray()
    }
}
