package com.demo.demoproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.demoproject.model.Movie
import com.demo.demoproject.repo.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewmodel(private val repository: MainRepository) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        fetchMovies()
    }

    private fun fetchMovies(){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getMovies()
                _isLoading.value = false
            } catch (throwable: Throwable){
                Log.e("SplashViewmodel", "Error fetching movies", throwable)
                _isLoading.value = false
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}