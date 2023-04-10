package com.example.demoapplication

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

/**
 * Singleton Object to handle chart configuration
 */
object DataService {
    /**
     * Create new DataSet for chart with custom default settings
     */
    fun newDataSet(name: String, color: Int): LineDataSet {
        val set = LineDataSet(ArrayList<Entry>(), name)
        set.setDrawCircles(false)
        set.setDrawValues(false)
        set.color = color
        set.lineWidth = 2.5f

        return set
    }

    /**
     * Set up chart using datasets for different axes of sensors
     */
    fun configureChart(name: String, chart: LineChart, isDarkMode: Boolean) {
        val xSet = newDataSet("x", Color.BLUE)
        val ySet = newDataSet("y", Color.RED)
        val zSet = newDataSet("z", Color.GREEN)

        val description = Description().apply {
            text = name
            textSize = 12f
            textColor = if (isDarkMode) Color.WHITE else Color.BLACK
        }

        chart.data = LineData(xSet, ySet, zSet)
        chart.description = description
        chart.description.isEnabled = true
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisLeft.setDrawLabels(false)
        chart.axisRight.setDrawLabels(false)
        chart.xAxis.setDrawLabels(false)

        if (isDarkMode) {
            chart.legend.textColor = Color.WHITE
        } else {
            chart.legend.textColor = Color.BLACK
        }

        chart.invalidate()
    }

    /**
     * Check if system is set to dark mode
     */
    fun isInDarkMode(context: Context): Boolean {
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }
}