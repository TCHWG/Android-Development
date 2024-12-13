package com.kuroakevizago.aira.ui.evaluation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.databinding.ActivityEvaluationBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.main.MainActivity
import com.kuroakevizago.aira.ui.music_player.MusicPlayerViewModel
import com.kuroakevizago.aira.utils.Tags
import com.kuroakevizago.aira.utils.fetchJsonFromUrl

class EvaluationActivity : AppCompatActivity() {
    private val viewModel by viewModels<EvaluationViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityEvaluationBinding

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        binding = ActivityEvaluationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.musicItem = intent.getParcelableExtra(Tags.MUSIC_TAG)
        viewModel.userEvaluation = intent.getParcelableExtra(Tags.EVALUATION_TAG)
        //viewModel.userAudioFile = intent.getParcelableExtra(Tags.EVALUATION_TAG)

        if (viewModel.musicItem == null || viewModel.userEvaluation == null) {
            finish()
        }

        setupAction()
        bindEvaluationData()
        viewModel.musicItem?.let { setupWebView(it) }
    }

    @SuppressLint("SetTextI18n")
    private fun bindEvaluationData() {
        val musicItem = viewModel.musicItem ?: return
        binding.titleText.text = musicItem.name

        val userEvaluation = viewModel.userEvaluation ?: return
        binding.dateText.text = "${userEvaluation.date} | ${userEvaluation.time}"

        val evaluation = userEvaluation.evaluations?.get(0) ?: return
        binding.summaryText.text = "Summary: ${evaluation.description}"
        binding.mistakeTypeText.text = "Mistake type: ${evaluation.name}"
    }

    @Suppress("DEPRECATION")
    @Deprecated("This method has been deprecated in favor of using the\n {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        // Create an intent to navigate to the home screen
        val homeIntent = Intent(this, MainActivity::class.java)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(homeIntent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    @Suppress("DEPRECATION")
    private fun setupAction() {
        binding.backButton.setOnClickListener {
            val homeIntent = Intent(this, MainActivity::class.java)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun setupWebView(musicItem: MusicItem) {
        val webView: WebView = binding.musicWebView
        val webSettings = webView.settings
        webSettings.apply {
            javaScriptEnabled = true
        }

        // Add a JavaScript interface for Kotlin communication
        webView.addJavascriptInterface(this, "AndroidBridge")

        // Load local HTML file
        webView.loadUrl("file:///android_asset/index.html")

        // Set WebViewClient to handle URL loading inside the WebView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (view != null && !musicItem.notePath.isNullOrEmpty()) {
                    initVisualizer(view, musicItem.notePath)
                }
            }
        }
    }

    private fun initVisualizer(webView: WebView, jsonUrl: String) {
        val evaluation = viewModel.userEvaluation?.evaluations?.get(0) ?: return
        val noteIndices: List<Int> = evaluation.mistakes?.mapNotNull {
            it?.noteIndex?.toIntOrNull()
        } ?: emptyList()

        // Fetch JSON from cloud
        fetchJsonFromUrl(jsonUrl) { json ->
            if (json != null) {
                // Pass JSON to JavaScript function
                webView.post {
                    webView.evaluateJavascript(
                        "javascript:initPlayerAndVisualizerHighlight($json, ${noteIndices})",
                        null
                    )
                }
            } else {
                // Handle error
                println("Failed to fetch JSON")
            }
        }
    }


    @JavascriptInterface
    fun onPlaybackStopped(isPlayingLastNote: Boolean, stopTime: Float, duration: Float) {

    }
}