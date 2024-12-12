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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.adapter.MusicVerticalViewAdapter
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.databinding.ActivityMusicPlayerBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.auth.login.LoginActivity
import com.kuroakevizago.aira.ui.evaluation.EvaluationActivity
import com.kuroakevizago.aira.utils.Tags
import com.kuroakevizago.aira.utils.Tags.Companion.EVALUATION_TAG
import com.kuroakevizago.aira.utils.Tags.Companion.MUSIC_FILE_TAG
import com.kuroakevizago.aira.utils.Tags.Companion.MUSIC_TAG
import com.kuroakevizago.aira.utils.addTouchScaleEffect
import com.kuroakevizago.aira.utils.fetchJsonFromUrl
import com.kuroakevizago.aira.utils.getAudioDuration
import com.kuroakevizago.aira.utils.setEnabledWithTransparency
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
            viewModel.userId = user.userId
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        checkPermissions()

        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.musicItem = intent.getParcelableExtra(MUSIC_TAG)
        val musicItem = viewModel.musicItem
        if (musicItem != null) {
            setupViewData(musicItem)
            setupWebView(musicItem)
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

        binding.playButton.addTouchScaleEffect()
        binding.restartButton.addTouchScaleEffect()
        binding.backButton.addTouchScaleEffect()
        binding.recordButton.addTouchScaleEffect()
        binding.evaluateButton.addTouchScaleEffect()

        binding.playButton.setOnClickListener {
            viewModel.isRecording = false
            toggleVisualizerPlay(webView)
        }

        binding.restartButton.setOnClickListener {
            toggleVisualizerPlay(webView, false)
            if (!viewModel.musicItem?.notePath.isNullOrEmpty()) {
                initVisualizer(webView, viewModel.musicItem?.notePath!!)
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

        binding.evaluateButton.setOnClickListener {
            evaluateRecording()
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
                val secondsLeft = (millisUntilFinished / 1000).toInt() + 1
                countdownTextView.text = secondsLeft.toString() // Update countdown text
            }

            override fun onFinish() {
                countdownTextView.text = "" // Clear the countdown text
                countdownTextView.visibility = View.GONE // Hide the TextView
                binding.recordButton.setEnabledWithTransparency(false)
                binding.playButton.setEnabledWithTransparency(false)
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
            binding.playButton.setEnabledWithTransparency(true)
            binding.recordButton.setEnabledWithTransparency(true)
            println("Recording saved temporarily at: ${viewModel.tempAudioFile?.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to stop recording")
        }
    }

    private fun evaluateRecording() {
        val musicItem = viewModel.musicItem
        val userId = viewModel.userId
        val audioFile = viewModel.tempAudioFile

        if (musicItem == null || userId.isNullOrEmpty() || audioFile == null) return

        viewModel.fetchEvaluationResponse(
            userId,
            musicItem.id.toString(),
            audioFile
        ).observe(this) {result ->
            when (result) {
                is ResultStatus.Error -> {
                    if (!viewModel.isEvaluateError) {
                        viewModel.isEvaluateError = true

                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.failed))
                            setMessage("${getString(R.string.something_went_wrong)}\n${result.error}")
                            setNegativeButton(getString(R.string.close)) { _, _ ->
                                viewModel.isEvaluateError = false
                                binding.progressLayout.visibility = View.INVISIBLE
                            }
                            create()
                            show()
                        }
                    }
                }
                is ResultStatus.Loading -> {
                    binding.progressLayout.visibility = View.VISIBLE
                }
                is ResultStatus.Success -> {
                    binding.progressLayout.visibility = View.INVISIBLE
                    val intent = Intent(this, EvaluationActivity::class.java)
                    intent.putExtra(MUSIC_TAG, musicItem)
                    intent.putExtra(EVALUATION_TAG, result.data.data)
                    //intent.putExtra(MUSIC_FILE_TAG, viewModel.tempAudioFile)
                    startActivity(intent)
                }
            }
        }
    }

    @JavascriptInterface
    fun onPlaybackStopped(isPlayingLastNote: Boolean, stopTime: Float, duration: Float) {
        if (isPlayingLastNote && viewModel.isRecording) {
            val remainingTime = (duration - stopTime).coerceAtLeast(0f) // Ensure non-negative delay
            if (remainingTime > 0.01) {
                // Add a delay based on the remaining time
                val delayMillis = (0.1 * 1000).toLong() // Convert to milliseconds
                println("Delay in millis $delayMillis")
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
            println("Music isn't finished, failed to record audio $stopTime")
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