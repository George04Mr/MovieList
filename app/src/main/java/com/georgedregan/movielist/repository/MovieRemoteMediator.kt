package com.georgedregan.movielist.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.georgedregan.movielist.database.MovieDatabase
import com.georgedregan.movielist.database.MovieEntity
import com.georgedregan.movielist.network.MovieApi

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val database: MovieDatabase,
    private val apiService: MovieApi,
    private val genre: String?,
    private val sortBy: String,
    private val sortOrder: String,
) : RemoteMediator<Int, MovieEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                val currentPage = (lastItem.id / state.config.pageSize) + 1
                currentPage
            }
        }

        return try {
            val response = apiService.getMovies(
                page = page,
                pageSize = state.config.pageSize,
                genre = genre,
                sortBy = sortBy,
                sortOrder = sortOrder
            )

            val movieEntities = response.map { it.toEntity() }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Optional: clear only filtered data
                    // database.movieDao().clearAll()
                }
                database.movieDao().insertAll(movieEntities)
            }

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
