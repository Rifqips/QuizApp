package com.example.permissionaccesskotlin

import android.app.KeyguardManager
import android.app.ProgressDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.permissionaccesskotlin.databinding.ActivityExamBinding
import java.util.concurrent.TimeUnit

class ExamActivity : AppCompatActivity() {

    private lateinit var binding : ActivityExamBinding

    private lateinit var myWebView: WebView
    private var countdownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var remainingTimeInMillis: Long = 0
    private val countdownDurationInMillis: Long = 60000 // 1 minute
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AlertDialog.Builder(this)
            .setTitle("Perhatian !")
            .setMessage("Notifikasi, Layar, dan lainnya akan terkunci, ingin melanjutkan ?")
            .setPositiveButton("Ya"){ dialogInterface: DialogInterface, i: Int ->
                startExam()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Tidak"){ dialogInterface: DialogInterface, i: Int ->
                finishAffinity()
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun startExam(){
        myWebView = findViewById(R.id.webView)
        myWebView.webViewClient = WebViewClient()
        myWebView.settings.setSupportZoom(true)
        myWebView.settings.loadsImagesAutomatically
        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = Callback()

        // Create and show a progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        myWebView.loadUrl("https://forms.gle/815wdJCPXsfwvirF8")
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                if (url.startsWith("intent://")) {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    if (intent != null) {
                        val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                        return if (fallbackUrl != null) {
                            webView.loadUrl(fallbackUrl)
                            progressDialog.dismiss()
                            true
                        } else {
                            false
                        }
                    }
                }
                else if (url.startsWith("tel:")){
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                    return true
                }
                else if (url.startsWith("mailto:")) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val data = Uri.parse(
                        url + Uri.encode("subject") + "&body=" + Uri.encode(
                            "body"
                        )
                    )
                    intent.data = data
                    startActivity(intent)
                    return true
                }
                return false
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
            myWebView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }
    private class Callback : WebViewClient() {
        override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
            return false
        }

    }

    fun startCountdown(){
        if (!isTimerRunning) {
            countdownTimer = object : CountDownTimer(countdownDurationInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    remainingTimeInMillis = millisUntilFinished
                }

                override fun onFinish() {
                    isTimerRunning = false
                    stopCountdown()
                    updateCountdownText()
                }
            }
            countdownTimer?.start()
            isTimerRunning = true
        }
    }

    fun stopCountdown() {
        countdownTimer?.cancel()
        isTimerRunning = false

        AlertDialog.Builder(this)
            .setTitle("Waktu Sudah Habis !")
            .setMessage("Silakan keluar dari ujian")
            .setPositiveButton("Ya"){ dialogInterface: DialogInterface, i: Int ->
                startActivity(Intent(this, InputTokenActivity::class.java))
                finishAndRemoveTask()
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun updateCountdownText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMillis - TimeUnit.MINUTES.toMillis(minutes))
        binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun turnedOfNotification() {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // if your webview can go back it will go back
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        // if your webview cannot go back
        // it will exit the application
        else
            super.onBackPressed()
    }
}