package com.georgedregan.movielist.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getAllPaged(): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movies")
    suspend fun getAll(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Update
    suspend fun update(movie: MovieEntity)

    @Delete
    suspend fun delete(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getById(id: Int): MovieEntity?

    @Query("DELETE FROM movies WHERE id = :movieId")
    suspend fun delete(movieId: Int)
}