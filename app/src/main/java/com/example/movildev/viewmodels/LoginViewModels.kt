package com.example.movildev.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movildev.repository.PreferencesRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val prefs: PreferencesRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<Result<Unit>>()
    val loginState: LiveData<Result<Unit>> get() = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Aquí debes buscar el usuario en tu fuente (local o API).
            // Simulamos que login es válido si email == "admin" y password == "1234"
            if (email == "admin" && password == "1234") {
                prefs.setLoggedInUserId(1)  // simula un ID de usuario
                _loginState.value = Result.success(Unit)
            } else {
                _loginState.value = Result.failure(Exception("Credenciales incorrectas"))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.clearSession()
        }
    }
}
