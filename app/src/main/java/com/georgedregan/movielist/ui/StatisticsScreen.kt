package com.georgedregan.movielist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.georgedregan.movielist.viewmodel.MovieViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: MovieViewModel,
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
