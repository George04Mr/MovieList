package com.georgedregan.movielist.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.Room
import com.georgedregan.movielist.database.MovieDatabase
import com.georgedregan.movielist.database.MovieEntity
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.network.MovieApi
import com.georgedregan.movielist.network.RetrofitClient
import com.georgedregan.movielist.paging.MoviePagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MovieRepository(context: Context) {
    private val apiService: MovieApi = RetrofitClient.api
    private val db = Room.databaseBuilder(
        context,
        MovieDatabase::class.java, "movie-database"
    ).build()
    private val movieDao = db.movieDao()

    fun getAllPaged(
        genre: String? = null,
        sortBy: String? = "id",
        sortOrder: String? = "asc"
    ): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            MoviePagingSource(apiService, movieDao, genre, sortBy.orEmpty(), sortOrder.orEmpty())
        }
    ).flow

    suspend fun getMovies(
        genre: String? = null,
        sortBy: String? = null,
        sortOrder: String? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): List<Movie> = withContext(Dispatchers.IO) {
        try {
            // Try to get from network first
            val networkMovies = apiService.getMovies(
                genre = genre,
                sortBy = sortBy,
                sortOrder = sortOrder,
                page = page,
                pageSize = pageSize
            )

            // Cache the movies locally
            movieDao.insertAll(networkMovies.map { it.toEntity() })

            // Return network movies
            networkMovies
        } catch (e: Exception) {
            // If network fails, return local movies
            movieDao.getAll().map { it.toMovie() }
        }
    }

    suspend fun addMovie(movie: Movie): Movie = withContext(Dispatchers.IO) {
        try {
            // Try network first
            val addedMovie = apiService.addMovie(movie)
            movieDao.insert(addedMovie.toEntity())
            return@withContext addedMovie
        } catch (e: Exception) {
            // Fallback to local storage
            val entity = movie.copy(id = 0).toEntity()
            val localId = movieDao.insert(entity)

            // Get the inserted entity with generated ID
            val insertedEntity =
                movieDao.getById(entity.id) ?: throw Exception("Failed to insert movie locally")

            // Convert back to Movie
            return@withContext insertedEntity.toMovie()
        }
    }

    suspend fun updateMovie(movie: Movie): Movie = withContext(Dispatchers.IO) {
        try {
            val updatedMovie = apiService.updateMovie(movie.id, movie)
            movieDao.update(updatedMovie.toEntity())
            updatedMovie
        } catch (e: Exception) {
            // If network fails, update locally
            movieDao.update(movie.toEntity())
            movie
        }
    }

    suspend fun deleteMovie(movieId: Int) = withContext(Dispatchers.IO) {
        try {
            apiService.deleteMovie(movieId)
            movieDao.delete(movieId)
        } catch (e: Exception) {
            // If network fails, mark for deletion locally
            movieDao.delete(movieId)
        }
    }

    private fun MovieEntity.toMovie(): Movie = Movie(
        id = this.id,
        title = this.title,
        year = this.year,
        genre = this.genre,
        imdbRating = this.imdbRating,
        distribution = this.distribution,
        posterUrl = this.posterUrl
    )
}

// Extension functions for conversion
fun Movie.toEntity(): MovieEntity = MovieEntity(
    id = this.id,
    title = this.title,
    year = this.year,
    genre = this.genre,
    imdbRating = this.imdbRating,
    distribution = this.distribution,
    posterUrl = this.posterUrl
)