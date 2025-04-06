package com.georgedregan.movielist.repository

import androidx.compose.runtime.mutableStateListOf
import com.georgedregan.movielist.model.Movie

class MovieRepository {
    private val movieList = mutableStateListOf<Movie>()

    fun getAllMovies(): List<Movie> = movieList.apply{
        addAll(getHardcodedMovies())
    }

    fun addMovie(movie: Movie) {
        movieList.add(movie)
    }

    fun updateMovie(updatedMovie: Movie) {
        val index = movieList.indexOfFirst { it.id == updatedMovie.id }
        if (index != -1) {
            movieList[index] = updatedMovie
        }
    }

    fun deleteMovie(movie: Movie) {
        movieList.removeAll { it.id == movie.id }
    }

    private fun getHardcodedMovies(): List<Movie> {
        return listOf(
            Movie(
                id = 1,
                title = "The Shawshank Redemption",
                year = 1994,
                posterUrl = "https://m.media-amazon.com/images/I/815qtzaP9iL._AC_UF894,1000_QL80_.jpg",
                genre = "Drama",
                imdbRating = 9.3,
                distribution = "Columbia Pictures"
            ),
            Movie(
                id = 2,
                title = "The Godfather",
                year = 1972,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg",
                genre = "Crime, Drama",
                imdbRating = 9.2,
                distribution = "Paramount Pictures"
            ),
            Movie(
                id = 3,
                title = "Pulp Fiction",
                year = 1994,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg",
                genre = "Crime, Drama",
                imdbRating = 8.9,
                distribution = "Miramax"
            ),
            Movie(
                id = 4,
                title = "The Dark Knight",
                year = 2008,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_.jpg",
                genre = "Action, Crime, Drama",
                imdbRating = 9.0,
                distribution = "Warner Bros."
            )
        )
    }
}