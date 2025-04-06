package com.georgedregan.movielist.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.ui.theme.customGreen
import com.georgedregan.movielist.ui.theme.darkGrayColor
import com.georgedregan.movielist.viewmodel.MovieViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieViewModel = viewModel(),
    onAddMovie: () -> Unit = {},
    onEditMovie: (Movie) -> Unit = {},
) {
    val selectedGenre by viewModel.selectedGenre
    val availableGenres by remember { derivedStateOf { viewModel.getAvailableGenres() } }
    val filteredMovies by remember { derivedStateOf { viewModel.getFilteredMovies() } }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = customGreen)) {
                                    append("Movie")
                                }
                                withStyle(style = SpanStyle(color = Color.White)) {
                                    append("List")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = darkGrayColor.copy(alpha = 0.9f)
                    )
                )

                // Add genre filter row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(darkGrayColor.copy(alpha = 0.7f))
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All genres filter
                    FilterChip(
                        selected = selectedGenre == null,
                        onClick = { viewModel.setGenreFilter(null) },
                        label = { Text("All", color = Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = customGreen,
                            selectedLabelColor = Color.White,
                            containerColor = darkGrayColor.copy(alpha = 0.5f)
                        )
                    )

                    // Genre filters
                    availableGenres.forEach { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { viewModel.setGenreFilter(genre) },
                            label = { Text(genre, color = Color.White) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = customGreen,
                                selectedLabelColor = Color.White,
                                containerColor = darkGrayColor.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMovie,
                containerColor = customGreen,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Movie", tint = Color.White)
            }
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                items(filteredMovies) { movie ->  // Use filteredMovies instead of movies
                    MovieCard(
                        movie = movie,
                        onEdit = { onEditMovie(movie) },
                        onDelete = { viewModel.deleteMovie(movie) }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MovieCard(
    movie: Movie,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(movie.posterUrl),
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .width(100.dp)
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = movie.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = customGreen
                )
                Text(text = "Year: ${movie.year}")

                // Improved genre display
                FlowRow(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    movie.genre.split(",").map { it.trim() }.forEach { genre ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(genre) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = darkGrayColor.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                Text(text = "IMDB: ${movie.imdbRating}")
                Text(text = "Distributor: ${movie.distribution}")
            }

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(140.dp)
                    .align(Alignment.CenterVertically)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = customGreen)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}