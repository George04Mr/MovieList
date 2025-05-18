package com.georgedregan.movielist.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.georgedregan.movielist.database.MovieDao
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.network.MovieApi
import com.georgedregan.movielist.repository.toEntity

class MoviePagingSource(
    private val api: MovieApi,
    private val movieDao: MovieDao,
    private val genre: String?,
    private val sortBy: String,
    private val sortOrder: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        return try {
            // Pass page and pageSize to API
            val movies = api.getMovies(genre, sortBy, sortOrder, page, pageSize)

            // Save to DB
            movieDao.insertAll(movies.map { it.toEntity() })

            // Determine if this is the last page
            val endOfPaginationReached = movies.size < pageSize

            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (endOfPaginationReached) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
