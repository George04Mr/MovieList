package com.georgedregan.movielist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.ui.theme.customGreen
import com.georgedregan.movielist.viewmodel.MovieViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMovieScreen(
    viewModel: MovieViewModel,
    movie: Movie? = null,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(movie?.title ?: "") }
    var year by remember { mutableStateOf(movie?.year?.toString() ?: "") }
    var posterUrl by remember { mutableStateOf(movie?.posterUrl ?: "") }
    var genre by remember { mutableStateOf(movie?.genre ?: "") }
    var imdbRating by remember { mutableStateOf(movie?.imdbRating?.toString() ?: "") }
    var distribution by remember { mutableStateOf(movie?.distribution ?: "") }

    // Validation states
    var titleError by remember { mutableStateOf<String?>(null) }
    var yearError by remember { mutableStateOf<String?>(null) }
    var posterUrlError by remember { mutableStateOf<String?>(null) }
    var genreError by remember { mutableStateOf<String?>(null) }
    var imdbRatingError by remember { mutableStateOf<String?>(null) }
    var distributionError by remember { mutableStateOf<String?>(null) }

    // Show error dialog state
    var showValidationErrorDialog by remember { mutableStateOf(false) }

    fun validateFields(): Boolean {
        titleError = if (title.isBlank()) "Title cannot be empty" else null
        yearError = when {
            year.isBlank() -> "Year cannot be empty"
            year.toIntOrNull() == null -> "Year must be a number"
            year.toInt() < 1888 -> "Year must be after 1888 (first movie)"
            year.toInt() > Calendar.getInstance().get(Calendar.YEAR) + 5 -> "Year cannot be in the future"
            else -> null
        }
        posterUrlError = if (posterUrl.isBlank()) "Poster URL cannot be empty" else null
        genreError = if (genre.isBlank()) "Genre cannot be empty" else null
        imdbRatingError = when {
            imdbRating.isBlank() -> "Rating cannot be empty"
            imdbRating.toDoubleOrNull() == null -> "Rating must be a number"
            imdbRating.toDouble() < 0 -> "Rating cannot be negative"
            imdbRating.toDouble() > 10 -> "Rating cannot be above 10"
            else -> null
        }
        distributionError = if (distribution.isBlank()) "Distributor cannot be empty" else null

        return listOf(
            titleError,
            yearError,
            posterUrlError,
            genreError,
            imdbRatingError,
            distributionError
        ).all { it == null }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (movie == null) "Add Movie" else "Edit Movie") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (validateFields()) {
                        val newMovie = Movie(
                            id = movie?.id ?: 0,
                            title = title,
                            year = year.toInt(),
                            posterUrl = posterUrl,
                            genre = genre,
                            imdbRating = imdbRating.toDouble(),
                            distribution = distribution
                        )

                        if (movie == null) {
                            viewModel.addMovie(newMovie)
                        } else {
                            viewModel.updateMovie(newMovie)
                        }
                        onBack()
                    } else {
                        showValidationErrorDialog = true
                    }
                },
                containerColor = customGreen
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) {
                            Text(text = titleError!!, color = Color.Red)
                        }
                    }
                )

                // Year Field
                OutlinedTextField(
                    value = year,
                    onValueChange = { if (it.length <= 4) year = it },
                    label = { Text("Year*") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = yearError != null,
                    supportingText = {
                        if (yearError != null) {
                            Text(text = yearError!!, color = Color.Red)
                        }
                    }
                )

                // Poster URL Field
                OutlinedTextField(
                    value = posterUrl,
                    onValueChange = { posterUrl = it },
                    label = { Text("Poster URL*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = posterUrlError != null,
                    supportingText = {
                        if (posterUrlError != null) {
                            Text(text = posterUrlError!!, color = Color.Red)
                        }
                    }
                )

                // Genre Field
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre* (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = genreError != null,
                    supportingText = {
                        if (genreError != null) {
                            Text(text = genreError!!, color = Color.Red)
                        }
                    }
                )

                // IMDB Rating Field
                OutlinedTextField(
                    value = imdbRating,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            imdbRating = it
                        }
                    },
                    label = { Text("IMDB Rating* (0-10)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = imdbRatingError != null,
                    supportingText = {
                        if (imdbRatingError != null) {
                            Text(text = imdbRatingError!!, color = Color.Red)
                        }
                    }
                )

                // Distribution Field
                OutlinedTextField(
                    value = distribution,
                    onValueChange = { distribution = it },
                    label = { Text("Distributor*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = distributionError != null,
                    supportingText = {
                        if (distributionError != null) {
                            Text(text = distributionError!!, color = Color.Red)
                        }
                    }
                )
            }

            // Validation Error Dialog
            if (showValidationErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showValidationErrorDialog = false },
                    title = { Text("Validation Error") },
                    text = { Text("Please fix all errors before saving") },
                    confirmButton = {
                        TextButton(
                            onClick = { showValidationErrorDialog = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = customGreen
                            )
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}