package com.example.demoapplication

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity to display result
 */
class ResultActivity : AppCompatActivity() {
    /**
     * UI elements
     */
    private lateinit var txtResult: TextView
    private lateinit var txtProbUser: TextView
    private lateinit var txtProbAttacker: TextView

    /**
     * Set up background colours
     */
    private val succColour = Color.parseColor("#4CAF50")
    private val failColour = Color.parseColor("#F44336")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_screen)

        // Get UI Elements
        txtResult = findViewById(R.id.txt_result)
        txtProbUser = findViewById(R.id.txt_prob_user)
        txtProbAttacker = findViewById(R.id.txt_prob_attacker)

        val result = intent.getIntExtra("result", -1)
        val probabilityUser = intent.getFloatExtra("userProbability", -1F)
        val probabilityAttacker = intent.getFloatExtra("attackerProbability", -1F)

        val rootView = findViewById<ViewGroup>(android.R.id.content)
        val bgColor = if (isUser(result.toFloat())) succColour else failColour
        rootView.setBackgroundColor(bgColor)

        txtResult.text = resultToString(result.toFloat())
        txtProbUser.text = "Probability User: $probabilityUser"
        txtProbAttacker.text = "Probability Attacker: $probabilityAttacker"

    }

    private fun isUser(res: Float): Boolean = res >= 0.5

    private fun resultToString(res: Float): String =
        getString(if (isUser(res)) R.string.result_positive else R.string.result_negative)
}