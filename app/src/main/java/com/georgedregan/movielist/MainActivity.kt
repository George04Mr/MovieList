package com.georgedregan.movielist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.ui.AddEditMovieScreen
import com.georgedregan.movielist.ui.Authentication.LoginScreen
import com.georgedregan.movielist.ui.Authentication.RegisterScreen
import com.georgedregan.movielist.ui.MovieDetailScreen
import com.georgedregan.movielist.ui.MovieListScreen
import com.georgedregan.movielist.ui.StatisticsScreen
import com.georgedregan.movielist.ui.theme.MovieListTheme
import com.georgedregan.movielist.ui.theme.darkGrayColor
import com.georgedregan.movielist.viewmodel.AuthViewModel
import com.georgedregan.movielist.viewmodel.MovieViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieListTheme {
                val authViewModel: AuthViewModel = viewModel()
                val isLoggedIn = authViewModel.isLoggedIn.value
                val currentUser = authViewModel.currentUser.value

                var currentScreen by remember {
                    mutableStateOf<Screen>(if (isLoggedIn) Screen.List else Screen.Login)
                }

                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var loginError by remember { mutableStateOf<String?>(null) }

                val viewModel = ViewModelProvider(this)[MovieViewModel::class.java]
                val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
                val coroutineScope = rememberCoroutineScope()
                val currentPage by remember { derivedStateOf { pagerState.currentPage } }

                var editingMovie by remember { mutableStateOf<Movie?>(null) }
                var selectedMovie by remember { mutableStateOf<Movie?>(null) }

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        currentScreen = Screen.List
                        loginError = null
                    }
                }

                when (currentScreen) {
                    Screen.Login -> LoginScreen(
                        username = username,
                        password = password,
                        onUsernameChange = { username = it },
                        onPasswordChange = { password = it },
                        onLoginClick = {
                            authViewModel.login(username, password)
                            loginError = if (authViewModel.currentUser.value == null) {
                                "Invalid login"
                            } else null
                        },
                        onRegisterNavigate = {
                            currentScreen = Screen.Register
                        },
                        errorMessage = loginError
                    )

                    Screen.Register -> RegisterScreen(
                        username = username,
                        password = password,
                        onUsernameChange = { username = it },
                        onPasswordChange = { password = it },
                        onRegisterClick = {
                            authViewModel.register(username, password)
                            currentScreen = Screen.Login
                        },
                        onBackToLogin = {
                            currentScreen = Screen.Login
                        }
                    )

                    Screen.List -> Scaffold(
                        bottomBar = {
                            NavigationBar(containerColor = darkGrayColor) {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                    selected = currentPage == 0,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                                    selected = currentPage == 1,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.padding(innerPadding),
                            pageContent = { page ->
                                when (page) {
                                    0 -> MovieListScreen(
                                        viewModel = viewModel,
                                        onAddMovie = {
                                            editingMovie = null
                                            currentScreen = Screen.AddEdit
                                        },
                                        onEditMovie = {
                                            editingMovie = it
                                            currentScreen = Screen.AddEdit
                                        },
                                        onClickMovie = {
                                            selectedMovie = it
                                            currentScreen = Screen.MovieDetails
                                        }
                                    )

                                    1 -> StatisticsScreen(viewModel = viewModel)
                                }
                            }
                        )
                    }

                    Screen.AddEdit -> AddEditMovieScreen(
                        viewModel = viewModel,
                        movie = editingMovie,
                        onBack = { currentScreen = Screen.List }
                    )

                    Screen.MovieDetails -> selectedMovie?.let { movie ->
                        MovieDetailScreen(
                            movie = movie,
                            onBack = { currentScreen = Screen.List },
                            onEditM = {
                                editingMovie = movie
                                currentScreen = Screen.AddEdit
                            }
                        )
                    } ?: run {
                        currentScreen = Screen.List
                    }
                }
            }
        }
    }
}


sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object List : Screen()
    object AddEdit : Screen()
    object MovieDetails : Screen()
}

