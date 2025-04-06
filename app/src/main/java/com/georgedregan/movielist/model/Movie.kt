package com.georgedregan.movielist.model

data class Movie(
    val id: Int,
    val title: String,
    val year: Int,
    val posterUrl: String,
    val genre: String,
    val imdbRating: Double,
    val distribution: String
)
