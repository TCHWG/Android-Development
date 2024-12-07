package com.kuroakevizago.aira.ui.music_player

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.adapter.MusicVerticalViewAdapter
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.databinding.ActivityMusicPlayerBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.auth.login.LoginActivity
import com.kuroakevizago.aira.utils.getAudioDuration
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException

class MusicPlayerActivity : AppCompatActivity() {

    private val viewModel by viewModels<MusicPlayerViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMusicPlayerBinding
    private val client = OkHttpClient()

    private var musicItem: MusicItem? = null

    private var mediaRecorder: MediaRecorder? = null

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        checkPermissions()

        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicItem = intent.getParcelableExtra(MusicVerticalViewAdapter.MUSIC_TAG)
        if (musicItem != null) {
            setupViewData(musicItem!!)
            setupWebView(musicItem!!)
            setupAction(binding.musicWebView)
        }

        if (viewModel.tempAudioFile?.exists() == true) {
            setEvaluateUIVisibility(false)
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        stopRecording()
        toggleVisualizerPlay(binding.musicWebView, false)
    }

    private fun setupViewData(musicItem: MusicItem) {
        binding.titleText.text = musicItem.name
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

    private fun setupAction(webView: WebView) {
        binding.playButton.setOnClickListener {
            toggleVisualizerPlay(webView)
        }

        binding.restartButton.setOnClickListener {
            toggleVisualizerPlay(webView, false)
            if (!musicItem?.notePath.isNullOrEmpty()) {
                initVisualizer(webView, musicItem?.notePath!!)
            }

            if (viewModel.tempAudioFile != null) {
                stopRecording()
                setEvaluateUIVisibility(false)
                viewModel.tempAudioFile = null
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.recordButton.setOnClickListener {
            if (viewModel.isRecording) {
                toggleVisualizerPlay(webView, false)
                stopRecording()
                setEvaluateUIVisibility(true)
            } else {
                toggleVisualizerPlay(webView, false)
                startCountdownAndRecord()
                setEvaluateUIVisibility(false)
            }
        }
    }


    @Suppress("DEPRECATION")
    private fun setupMediaRecorder() {
        if (viewModel.tempAudioFile != null) {
            setEvaluateUIVisibility(true)
        }

        // Create a temporary file in the cache directory
        viewModel.tempAudioFile = File.createTempFile("recorded_music", ".mp3", cacheDir)
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // Use an efficient format for temp storage
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(viewModel.tempAudioFile?.absolutePath) // Set the temp file path
        }
    }

    private fun startCountdownAndRecord() {
        val countdownTextView: TextView = binding.countdownText

        // Make the TextView visible
        countdownTextView.visibility = View.VISIBLE
        setupMediaRecorder()

        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                countdownTextView.text = secondsLeft.toString() // Update countdown text
            }

            override fun onFinish() {
                countdownTextView.text = "" // Clear the countdown text
                countdownTextView.visibility = View.GONE // Hide the TextView
                startRecording() // Start recording after the countdown
                toggleRecordButtonIcon()
                toggleVisualizerPlay(binding.musicWebView)
            }
        }.start()
    }

    private fun startRecording() {
        try {
            viewModel.isRecording = true
            toggleRecordButtonIcon()
            mediaRecorder?.apply {
                prepare() // Prepare the recorder
                start() // Start recording
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        try {
            viewModel.isRecording = false
            toggleRecordButtonIcon()
            mediaRecorder?.apply {
                stop() // Stop recording
                release() // Release resources
            }
            mediaRecorder = null
            Toast.makeText(this, "Recording saved temporarily at: ${viewModel.tempAudioFile?.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Failed to stop recording",
                Toast.LENGTH_SHORT).
            show()
        }
    }

    @JavascriptInterface
    fun onPlaybackStopped(isPlayingLastNote: Boolean, stopTime: Float, duration: Float) {
        if (isPlayingLastNote) {
            val remainingTime = (duration - stopTime).coerceAtLeast(0f) // Ensure non-negative delay

            if (remainingTime > 0.01) {
                // Add a delay based on the remaining time
                val delayMillis = (remainingTime * 1000).toLong() // Convert to milliseconds
                Toast.makeText(
                    this,
                    "Delay in milis $delayMillis",
                    Toast.LENGTH_LONG
                ).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    // Recheck if the playback has reached the end
                    if (stopTime + remainingTime >= duration) {
                        stopRecording()
                        setEvaluateUIVisibility(true)
                    } else {
                        println("Playback was interrupted before reaching the end.")
                    }
                }, delayMillis)
            } else {
                // No remaining time, finalize immediately
                stopRecording()
                setEvaluateUIVisibility(true)
            }
        } else {
            Toast.makeText(
                this,
                "Music has isn't finished, failed to record audio",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initVisualizer(webView: WebView, jsonUrl: String) {
        // Fetch JSON from cloud
        fetchJsonFromUrl(jsonUrl) { json ->
            if (json != null) {

                // Pass JSON to JavaScript function
                webView.post {
                    webView.evaluateJavascript("javascript:initPlayerAndVisualizer($json)", null)
                }
            } else {
                // Handle error
                println("Failed to fetch JSON")
            }
        }
    }

    private fun fetchJsonFromUrl(url: String, callback: (String?) -> Unit) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let {
                        callback(it.string())
                    }
                } else {
                    callback(null)
                }
            }
        })
    }

    private fun toggleVisualizerPlay(webView: WebView, isPlay: Boolean = true) {
        if (isPlay) {
            webView.evaluateJavascript("javascript:start()", null)
        } else {
            webView.evaluateJavascript("javascript:stop()", null)
        }

    }

    private fun toggleRecordButtonIcon() {
        val newIcon = if (viewModel.isRecording) {
            R.drawable.ic_radio_button_unchecked_24px // Stop icon
        } else {
            R.drawable.ic_radio_button_checked_24px // Record icon
        }

        // Scale down and back up
        binding.recordButton.animate().scaleX(0.5f).scaleY(0.5f).setDuration(150)
            .withEndAction {
                // Set new icon when scaled down
                binding.recordButton.setImageResource(newIcon)

                // Scale back to normal
                binding.recordButton.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
            }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun setEvaluateUIVisibility(visible: Boolean) {
        binding.evaluateLayout.visibility = if (visible) View.VISIBLE else View.GONE

        val recordedFile = viewModel.tempAudioFile

        if (visible && recordedFile?.exists() == true) {
            val filePath = recordedFile.absolutePath // Extract the full file path

            val audioDuration = getAudioDuration(filePath)
            binding.fileNameText.text = "Recorded_file | Duration: $audioDuration"
        }
    }



    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_CODE_PERMISSIONS
            )
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
    }
}