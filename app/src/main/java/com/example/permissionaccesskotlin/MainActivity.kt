package com.example.permissionaccesskotlin

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var countdownTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    private var countdownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var remainingTimeInMillis: Long = 0
    private val countdownDurationInMillis: Long = 60000 // 1 minute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lockScreen()

        countdownTextView = findViewById(R.id.countdownTextView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        startButton.isEnabled = true
        stopButton.isEnabled = false

    }

    private fun lockScreen() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // Kunci layar hanya dapat dikunci jika versi Android >= O_MR1
            keyguardManager.requestDismissKeyguard(this, null)
        }
    }
    fun startCountdown(view: View) {
        if (!isTimerRunning) {
            countdownTimer = object : CountDownTimer(countdownDurationInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    remainingTimeInMillis = millisUntilFinished
                    updateCountdownText()
                }

                override fun onFinish() {
                    isTimerRunning = false
                    updateCountdownText()
                    startButton.isEnabled = true
                    stopButton.isEnabled = false
                }
            }

            countdownTimer?.start()
            isTimerRunning = true
            startButton.isEnabled = false
            stopButton.isEnabled = true
        }
    }

    fun stopCountdown(view: View) {
        countdownTimer?.cancel()
        isTimerRunning = false
        startButton.isEnabled = true
        stopButton.isEnabled = false
        updateCountdownText()
    }

    private fun updateCountdownText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMillis - TimeUnit.MINUTES.toMillis(minutes))
        countdownTextView.text = String.format("%02d:%02d", minutes, seconds)
    }
}