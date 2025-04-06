package com.georgedregan.movielist.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.repository.MovieRepository

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()
    private var nextId = repository.getAllMovies().maxOfOrNull { it.id }?.plus(1) ?: 1

    private val _movies = mutableStateOf(repository.getAllMovies())
    val movies: State<List<Movie>> = _movies

    private val _selectedGenre = mutableStateOf<String?>(null)
    val selectedGenre: State<String?> = _selectedGenre

    fun addMovie(movie: Movie) {
        val newMovie = movie.copy(id = nextId++)
        repository.addMovie(newMovie)
        _movies.value = repository.getAllMovies()
    }

    fun updateMovie(movie: Movie) {
        repository.updateMovie(movie)
        _movies.value = repository.getAllMovies()
    }

    fun deleteMovie(movie: Movie) {
        repository.deleteMovie(movie)
        _movies.value = repository.getAllMovies()
    }

    fun setGenreFilter(genre: String?) {
        _selectedGenre.value = genre
    }

    fun getAvailableGenres(): List<String> {
        return _movies.value.map { it.genre }.distinct()
    }

    fun getFilteredMovies(): List<Movie> {
        return _selectedGenre.value?.let { genre ->
            _movies.value.filter { it.genre == genre }
        } ?: _movies.value
    }
}