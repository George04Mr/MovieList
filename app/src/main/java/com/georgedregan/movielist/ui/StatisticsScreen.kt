package com.georgedregan.movielist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.georgedregan.movielist.viewmodel.MovieViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun StatisticsScreen(
    viewModel: MovieViewModel,
) {
    val movies by viewModel.movies
    val colorScheme = MaterialTheme.colorScheme

    // Calculate statistics
    val (stats, totalMovies) = remember(movies) {
        val counts = mutableMapOf(
            "0-3" to 0,
            "3-6" to 0,
            "6-8" to 0,
            "8-10" to 0
        )

        movies.forEach { movie ->
            when {
                movie.imdbRating < 3 -> counts["0-3"] = counts["0-3"]!! + 1
                movie.imdbRating < 6 -> counts["3-6"] = counts["3-6"]!! + 1
                movie.imdbRating < 8 -> counts["6-8"] = counts["6-8"]!! + 1
                else -> counts["8-10"] = counts["8-10"]!! + 1
            }
        }
        Pair(counts, movies.size)
    }

    // Prepare colors
    val barColors = remember(colorScheme) {
        listOf(
            colorScheme.primary.toArgb(),
            colorScheme.secondary.toArgb(),
            colorScheme.tertiary.toArgb(),
            colorScheme.primaryContainer.toArgb()
        )
    }
    var valueTextColor = remember(colorScheme) {
        colorScheme.onSurface.toArgb()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            "Movie Statistics",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Statistics summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Rating Distribution",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total movies: $totalMovies")
                Spacer(modifier = Modifier.height(8.dp))
                Text("0-3 stars: ${stats["0-3"]} (${percentage(stats["0-3"]!!, totalMovies)}%)")
                Text("3-6 stars: ${stats["3-6"]} (${percentage(stats["3-6"]!!, totalMovies)}%)")
                Text("6-8 stars: ${stats["6-8"]} (${percentage(stats["6-8"]!!, totalMovies)}%)")
                Text("8-10 stars: ${stats["8-10"]} (${percentage(stats["8-10"]!!, totalMovies)}%)")
            }
        }

        // Bar chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        // Configure chart appearance
                        description.isEnabled = false
                        setDrawGridBackground(false)
                        setDrawBarShadow(false)
                        setDrawValueAboveBar(true)
                        setPinchZoom(false)
                        setDrawBorders(false)
                        setScaleEnabled(false)

                        // Configure x-axis
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            granularity = 1f
                            isGranularityEnabled = true
                            labelCount = 4
                            valueFormatter = IndexAxisValueFormatter(listOf("0-3", "3-6", "6-8", "8-10"))
                        }

                        // Configure left axis
                        axisLeft.apply {
                            setDrawGridLines(true)
                            axisMinimum = 0f
                            granularity = 1f
                            isGranularityEnabled = true
                        }

                        // Configure right axis
                        axisRight.isEnabled = false

                        // Prepare data
                        val entries = listOf(
                            BarEntry(0f, stats["0-3"]!!.toFloat()),
                            BarEntry(1f, stats["3-6"]!!.toFloat()),
                            BarEntry(2f, stats["6-8"]!!.toFloat()),
                            BarEntry(3f, stats["8-10"]!!.toFloat())
                        )

                        val dataSet = BarDataSet(entries, "Movies by Rating").apply {
                            colors = barColors
                            valueTextColor = valueTextColor
                            valueTextSize = 12f
                            setDrawValues(true)
                        }

                        data = BarData(dataSet).apply {
                            barWidth = 0.5f
                        }

                        animateY(1000)
                        invalidate()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun percentage(count: Int, total: Int): String {
    return if (total == 0) "0" else "%.1f".format(count.toFloat() / total * 100)
}