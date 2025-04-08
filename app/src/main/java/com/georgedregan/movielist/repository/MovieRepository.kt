package com.georgedregan.movielist.repository

import androidx.compose.runtime.mutableStateListOf
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.network.RetrofitClient
import retrofit2.Retrofit

class MovieRepository {
    suspend fun getAllMovies(
        genre: String? = null,
        sortBy: String? = null,
        sortOrder: String? = null
    ): List<Movie> {
        return RetrofitClient.api.getMovies(
            genre = genre,
            sortBy = sortBy,
            sortOrder = sortOrder
        )
    }

    suspend fun addMovie(movie: Movie): Movie {
        return RetrofitClient.api.addMovie(movie)
    }

    suspend fun updateMovie(movie: Movie): Movie {
        return RetrofitClient.api.updateMovie(movie.id, movie)
    }

    suspend fun deleteMovie(movie: Movie) {
        RetrofitClient.api.deleteMovie(movie.id)
    }
}