package com.georgedregan.movielist.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.network.MovieApi

class MoviePagingSource(
    private val api: MovieApi,
    private val genre: String?,
    private val sortBy: String,
    private val sortOrder: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val movies = api.getMovies(genre, sortBy, sortOrder)
            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (movies.isEmpty()) null else page + 1
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
