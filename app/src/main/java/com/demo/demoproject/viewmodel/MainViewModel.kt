package com.demo.demoproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.demoproject.constants.Constants
import com.demo.demoproject.constants.Constants.RECENT_SEARCHES_KEY
import com.demo.demoproject.datastore.DataStoreManager
import com.demo.demoproject.model.Movie
import com.demo.demoproject.repo.MainRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {


    private val _moviesListState = MutableStateFlow<List<Movie>>(emptyList())
    val moviesListState = _moviesListState.asStateFlow()

    private val _recentSearchesState = MutableStateFlow<List<String>>(emptyList())
    val recentSearchesState = _recentSearchesState.asStateFlow()

    init {
        getMovies()
    }

    fun getMovies(){
        viewModelScope.launch {
            try {
                repository.getAllMovies().collect {
                    _moviesListState.value = it
                }
            }catch (throwable: Throwable){
               Log.d("TAG", "getMovies: ${throwable.message}")
            }
        }
    }
    fun getFavouriteMovies(){
        viewModelScope.launch {
            try {
                _moviesListState.value = emptyList()
                repository.getFavouriteMovies().collect {
                    _moviesListState.value = it
                }
            }catch (throwable: Throwable){
                Log.d("TAG", "getMovies: ${throwable.message}")
            }
        }
    }
    fun filterMoviesByLanguages(languages: List<String>){
        viewModelScope.launch {
            try {
                _moviesListState.value = emptyList()
                repository.filterMoviesByLanguages(languages).collect {
                    _moviesListState.value = it
                }
            }catch (throwable: Throwable){
                Log.d("TAG", "getMovies: ${throwable.message}")
            }
        }
    }
    fun filterMoviesByVoteCount(voteCounts: List<Int>){
        viewModelScope.launch {
            try {
                _moviesListState.value = emptyList()
                repository.filterMoviesByVoteCount(voteCounts).collect {
                    _moviesListState.value = it
                }
            }catch (throwable: Throwable){
                Log.d("TAG", "getMovies: ${throwable.message}")
            }
        }
    }
    fun searchMoviesByTitle(searchQuery: String) {
        if (searchQuery.isBlank()) {
            getMovies()
            return
        }
        viewModelScope.launch {
            try {
                maintainRecentSearches(searchQuery)
                _moviesListState.value = emptyList()
                repository.searchMoviesByTitle(searchQuery).collect {
                    _moviesListState.value = it
                }
            } catch (throwable: Throwable) {
                Log.e("TAG", "Error in searchMoviesByTitle", throwable)
            }
        }
    }

    suspend fun getRecentSearches() {
        val recentSearchesKey = RECENT_SEARCHES_KEY
        val gson = Gson()
        val currentSearchesJson = dataStoreManager.getString(recentSearchesKey).firstOrNull()
        val recentSearches: MutableList<String> = if (currentSearchesJson.isNullOrBlank()) {
            mutableListOf()
        } else {
            try {
                val typeToken = object : TypeToken<List<String>>() {}.type
                gson.fromJson(currentSearchesJson, typeToken) ?: mutableListOf()
            } catch (e: Exception) {
                Log.e("ViewModel", "Error parsing recent searches JSON", e)
                mutableListOf()
            }
        }
        _recentSearchesState.value = recentSearches
    }
    private suspend fun maintainRecentSearches(searchQuery: String) {
        val recentSearchesKey = RECENT_SEARCHES_KEY
        val gson = Gson()

        val currentSearchesJson = dataStoreManager.getString(recentSearchesKey).firstOrNull()
        val recentSearches: MutableList<String> = if (currentSearchesJson.isNullOrBlank()) {
            mutableListOf()
        } else {
            try {
                val typeToken = object : TypeToken<List<String>>() {}.type
                gson.fromJson(currentSearchesJson, typeToken) ?: mutableListOf()
            } catch (e: Exception) {
                Log.e("ViewModel", "Error parsing recent searches JSON", e)
                mutableListOf()
            }
        }
        recentSearches.removeAll { it.equals(searchQuery, ignoreCase = true) }
        recentSearches.add(0, searchQuery)
        while (recentSearches.size > Constants.MAX_RECENT_SEARCHES) {
            recentSearches.removeAt(recentSearches.lastIndex)
        }
        val updatedSearchesJson = gson.toJson(recentSearches)
        dataStoreManager.saveString(recentSearchesKey, updatedSearchesJson)
    }

    fun updateMovie(movie: Movie){
        viewModelScope.launch {
            try {
                repository.insertSingle(movie)
            }catch (throwable: Throwable){
               Log.d("TAG", "getMovies: ${throwable.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
