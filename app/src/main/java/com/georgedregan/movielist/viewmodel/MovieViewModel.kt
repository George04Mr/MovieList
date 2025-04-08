package com.georgedregan.movielist.viewmodel

import android.view.View
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.network.RetrofitClient
import com.georgedregan.movielist.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()

    private val _movies = mutableStateOf(emptyList<Movie>())
    val movies: State<List<Movie>> = _movies

    private val _selectedGenre = mutableStateOf<String?>(null)
    val selectedGenre: State<String?> = _selectedGenre

    private val _selectedYear = mutableStateOf<Int?>(null)
    private val _sortBy = mutableStateOf("id")
    private val _sortOrder = mutableStateOf("asc")

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        getMovies()
    }

    private fun getMovies() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _movies.value = repository.getAllMovies(
                    genre = _selectedGenre.value,
                    sortBy = _sortBy.value,
                    sortOrder = _sortOrder.value
                )
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load movies: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                repository.addMovie(movie)
                getMovies() // Refresh the list
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add movie: ${e.message}"
            }
        }
    }

    fun updateMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                repository.updateMovie(movie)
                getMovies() // Refresh the list
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update movie: ${e.message}"
            }
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                repository.deleteMovie(movie)
                getMovies() // Refresh the list
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete movie: ${e.message}"
            }
        }
    }

    fun setGenreFilter(genre: String?) {
        _selectedGenre.value = genre
        getMovies()
    }

    fun setYearFilter(year: Int?) {
        _selectedYear.value = year
        getMovies()
    }

    fun setSort(sortBy: String, sortOrder: String) {
        _sortBy.value = sortBy
        _sortOrder.value = sortOrder
        getMovies()
    }

    fun getAvailableGenres(): List<String> {
        return _movies.value.map { it.genre }.distinct()
    }

    fun getAvailableYears(): List<Int> {
        return _movies.value.map { it.year }.distinct().sorted()
    }
}