package com.georgedregan.movielist.network

import com.georgedregan.movielist.model.Movie
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("movies")
    suspend fun getMovies(
        @Query("genre") genre: String? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("sort_order") sortOrder: String? = null
    ): List<Movie>

    @POST("movies")
    suspend fun addMovie(@Body movie: Movie): Movie

    @PATCH("movies/{id}")
    suspend fun updateMovie(
        @Path("id") id: Int,
        @Body movie: Movie
    ): Movie

    @DELETE("movies/{id}")
    suspend fun deleteMovie(@Path("id") id: Int)
}