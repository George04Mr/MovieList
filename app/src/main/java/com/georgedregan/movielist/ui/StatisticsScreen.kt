package com.georgedregan.movielist.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.georgedregan.movielist.model.Movie
import com.georgedregan.movielist.ui.theme.customGreen
import com.georgedregan.movielist.ui.theme.darkGrayColor
import com.georgedregan.movielist.viewmodel.MovieViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: MovieViewModel,
    onBack: () -> Unit
) {
    val movies by viewModel.movies

    // Correct stats calculation
    val stats = remember(movies) {
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
        counts
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Add the chart library dependency to your build.gradle:
            // implementation "com.github.PhilJay:MPAndroidChart:v3.1.0"

            // Bar chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
            ) {
                AndroidView(
                    factory = { context ->
                        BarChart(context).apply {
                            // Configure chart
                            description.isEnabled = false
                            setDrawGridBackground(false)
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            axisLeft.setDrawGridLines(false)
                            axisRight.setDrawGridLines(false)
                            legend.isEnabled = false

                            // Prepare data
                            val entries = listOf(
                                BarEntry(0f, stats["0-3"]!!.toFloat()),
                                BarEntry(1f, stats["3-6"]!!.toFloat()),
                                BarEntry(2f, stats["6-8"]!!.toFloat()),
                                BarEntry(3f, stats["8-10"]!!.toFloat())
                            )

                            val dataSet = BarDataSet(entries, "Ratings").apply {
                                colors = ColorTemplate.MATERIAL_COLORS.toList()
                                valueTextColor = android.graphics.Color.BLACK
                                valueTextSize = 12f
                            }

                            val barData = BarData(dataSet)
                            data = barData

                            // X-axis labels
                            val xLabels = listOf("0-3", "3-6", "6-8", "8-10")
                            xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)

                            animateY(1000)
                        }
                    }
                )
            }

            // Statistics counts
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Movie Statistics", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("0-3 count: ${stats["0-3"]}")
                Text("3-6 count: ${stats["3-6"]}")
                Text("6-8 count: ${stats["6-8"]}")
                Text("8-10 count: ${stats["8-10"]}")
            }
        }
    }
}