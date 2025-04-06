package com.georgedregan.movielist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.georgedregan.movielist.ui.MovieListScreen
import com.georgedregan.movielist.ui.theme.MovieListTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.ui.AddEditMovieScreen
import com.georgedregan.movielist.ui.StatisticsScreen
import com.georgedregan.movielist.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieListTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Statistics) }
                var editingMovie by remember { mutableStateOf<Movie?>(null) }
                val viewModel: MovieViewModel = viewModel()

                when (currentScreen) {
                    Screen.List -> MovieListScreen(
                        viewModel = viewModel,
                        onAddMovie = {
                            editingMovie = null
                            currentScreen = Screen.AddEdit
                        },
                        onEditMovie = { movie ->
                            editingMovie = movie
                            currentScreen = Screen.AddEdit
                        },
                        onStatsClick = { currentScreen = Screen.Statistics }
                    )
                    Screen.AddEdit -> AddEditMovieScreen(
                        viewModel = viewModel,
                        movie = editingMovie,
                        onBack = { currentScreen = Screen.List }
                    )
                    Screen.Statistics -> StatisticsScreen(  // New screen
                        viewModel = viewModel,
                        onBack = { currentScreen = Screen.List }
                    )
                }
            }
        }
    }
}

sealed class Screen {
    object List : Screen()
    object AddEdit : Screen()
    object Statistics : Screen()
}

