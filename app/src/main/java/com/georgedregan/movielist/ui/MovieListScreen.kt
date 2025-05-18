package com.georgedregan.movielist.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.rememberAsyncImagePainter
import com.georgedregan.movielist.R
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
    onClickMovie: (Movie) -> Unit = {}
) {
    val movies: LazyPagingItems<Movie> = viewModel.moviesPagedData.collectAsLazyPagingItems()
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val selectedGenre by viewModel.selectedGenre
    val availableGenres by remember { derivedStateOf { viewModel.getAvailableGenres() } }
    val isNetworkAvailable by viewModel.isNetworkAvailable
    val isServerAvailable by viewModel.isServerAvailable

    // Network status banner
    val showNetworkBanner by remember {
        derivedStateOf { !isNetworkAvailable || !isServerAvailable }
    }

    // State for sort options
    var showSortDialog by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf("id") }
    var selectedSortOrder by remember { mutableStateOf("asc") }

    // Handle error messages
    if (!errorMessage.isNullOrEmpty()) {
        LaunchedEffect(errorMessage) {
            //scaffoldState.snackbarHostState.showSnackbar(errorMessage!!)
            //viewModel.clearErrorMessage()
        }
    }

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
                    ),
                    actions = {
                        IconButton(onClick = { showSortDialog = true }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_filter_list_24),
                                contentDescription = "Filter Icon",
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(Color.Green)
                            )
                        }
                    }
                )

                // Filter chips row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(darkGrayColor.copy(alpha = 0.7f))
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All filter
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
            Column {
                if (showNetworkBanner) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (!isNetworkAvailable) Color.Red else Color.Yellow)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!isNetworkAvailable) "Network unavailable - working offline"
                            else "Server unavailable - working offline",
                            color = Color.Black
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
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = customGreen
                    )
                } else {
                    LazyColumn(
                        contentPadding = innerPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        items(
                            count = movies.itemCount,
                            key = movies.itemKey { it.id },
                            contentType = movies.itemContentType{"Movies"}
                        ) { index: Int ->
                            val movie: Movie? = movies[index]
                            movie?.let {
                                MovieCard(
                                    movie = movie,
                                    onEdit = { onEditMovie(movie) },
                                    onDelete = { viewModel.deleteMovie(movie) },
                                    onClick = { onClickMovie(movie) }
                                )
                            }
                        }
                        item {
                            if (movies.loadState.append is androidx.paging.LoadState.Loading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = customGreen)
                                }
                            }
                        }

                        // Error handling on pagination
                        item {
                            val appendError = movies.loadState.append as? androidx.paging.LoadState.Error
                            appendError?.let {
                                Text(
                                    text = "Error loading more movies: ${it.error.localizedMessage}",
                                    color = Color.Red,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                    }
                }
            }
        }
    )

    // Sort dialog
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("Sort Movies") },
            text = {
                Column {
                    Text("Sort by:", modifier = Modifier.padding(bottom = 8.dp))
                    RadioGroup(
                        selectedOption = selectedSortOption,
                        options = listOf(
                            "Title" to "title",
                            "Year" to "year",
                            "Rating" to "rating"
                        ),
                        onOptionSelected = { selectedSortOption = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Order:", modifier = Modifier.padding(bottom = 8.dp))
                    RadioGroup(
                        selectedOption = selectedSortOrder,
                        options = listOf("Ascending" to "asc", "Descending" to "desc"),
                        onOptionSelected = { selectedSortOrder = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setSort(selectedSortOption, selectedSortOrder)
                        showSortDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = customGreen)
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RadioGroup(
    selectedOption: String,
    options: List<Pair<String, String>>,
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { (label, value) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(value) }
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = selectedOption == value,
                    onClick = { onOptionSelected(value) },
                    colors = RadioButtonDefaults.colors(selectedColor = customGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MovieCard(
    movie: Movie,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
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

                Text(text = "IMDB: ${String.format("%.1f", movie.imdbRating)}")
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