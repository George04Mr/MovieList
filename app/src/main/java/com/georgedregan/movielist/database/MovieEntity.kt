package com.georgedregan.movielist.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("year")
    val year: Int,
    @ColumnInfo("genre")
    val genre: String,
    @ColumnInfo("imdbRating")
    val imdbRating: Double,
    @ColumnInfo("distribution")
    val distribution: String,
    @ColumnInfo("posterUrl")
    val posterUrl: String
)