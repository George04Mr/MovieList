package com.georgedregan.movielist.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.network.RetrofitClient
import com.georgedregan.movielist.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class MovieViewModel(private val application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepository(application.applicationContext)

    private val _movies = mutableStateOf<List<Movie>>(emptyList())
    val movies: State<List<Movie>> = _movies

    private val _currentPage = mutableStateOf(1)
    private val _allMoviesLoaded = mutableStateOf(false)
    val allMoviesLoaded: State<Boolean> = _allMoviesLoaded

    private val _selectedGenre = mutableStateOf<String?>(null)
    val selectedGenre: State<String?> = _selectedGenre

    private val _sortBy = mutableStateOf("id")
    private val _sortOrder = mutableStateOf("asc")

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _isNetworkAvailable = mutableStateOf(true)
    val isNetworkAvailable: State<Boolean> = _isNetworkAvailable

    private val _isServerAvailable = mutableStateOf(true)
    val isServerAvailable: State<Boolean> = _isServerAvailable

    val moviesPagedData =
        repository.getAllPaged().cachedIn(viewModelScope)

    init {
//        checkNetworkStatus()
//        checkServerStatus()
        loadMovies()
    }

    private fun checkServerStatus() {
        viewModelScope.launch {
            while (true) {
                try {
                    val isAvailable = withContext(Dispatchers.IO) {
                        try {
                            // Add timeout to prevent hanging
                            withTimeout(3000) {
                                RetrofitClient.api.ping()
                            }
                        } catch (e: Exception) {
                            false
                        }
                    }
                    _isServerAvailable.value = isAvailable
                } catch (e: Exception) {
                    _isServerAvailable.value = false
                }
                delay(5000) // Check every 5 seconds
            }
        }
    }

    private fun checkNetworkStatus() {
        viewModelScope.launch {
            while (true) {
                _isNetworkAvailable.value = withContext(Dispatchers.IO) {
                    val connectivityManager =
                        application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val networkInfo = connectivityManager.activeNetworkInfo
                    networkInfo != null && networkInfo.isConnected
                }
                delay(5000)
            }
        }
    }

    fun loadMoreMovies() {
        if (_isLoading.value || _allMoviesLoaded.value) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (isNetworkAvailable.value && isServerAvailable.value) {
                    val newMovies = repository.getMovies(page = _currentPage.value + 1)
                    if (newMovies.isNotEmpty()) {
                        _movies.value = _movies.value + newMovies
                        _currentPage.value += 1
                    } else {
                        _allMoviesLoaded.value = true
                    }
                }
                // No else case since we don't paginate local data
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load more movies: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMovies() {
        if (_isLoading.value) return // Prevent multiple simultaneous loads

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val newMovies = repository.getMovies(
                    genre = _selectedGenre.value,
                    sortBy = _sortBy.value,
                    sortOrder = _sortOrder.value
                )
                _movies.value = newMovies
                _currentPage.value = 1 // Reset pagination when refreshing
                _allMoviesLoaded.value = false
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load movies: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMovie(movie: Movie) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val addedMovie = repository.addMovie(movie)
                _movies.value = _movies.value + addedMovie
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add movie: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMovie(movie: Movie) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val updatedMovie = repository.updateMovie(movie)
                _movies.value = _movies.value.map { if (it.id == movie.id) updatedMovie else it }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update movie: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMovie(movie: Movie) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.deleteMovie(movie.id)
                _movies.value = _movies.value.filter { it.id != movie.id }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete movie: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setGenreFilter(genre: String?) {
        _selectedGenre.value = genre
        loadMovies()
    }

    fun setSort(sortBy: String, sortOrder: String) {
        _sortBy.value = sortBy
        _sortOrder.value = sortOrder
        loadMovies()
    }

    fun getAvailableGenres(): List<String> {
        return _movies.value
            .flatMap { it.genre.split(",") }
            .map { it.trim() }
            .distinct()
    }
}