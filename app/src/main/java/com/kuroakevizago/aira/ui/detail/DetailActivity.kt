package com.kuroakevizago.aira.ui.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.adapter.MusicVerticalViewAdapter.Companion.MUSIC_TAG
import com.kuroakevizago.aira.data.local.entity.MusicEntity
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.databinding.ActivityDetailBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.auth.login.LoginActivity
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.ui.music_player.MusicPlayerActivity

class DetailActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityDetailBinding
    private var currentData: MusicItem? = null
    private var userId: String? = null
    private var isBookmarked = false

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the music item from intent
        val musicItem = intent.getParcelableExtra<MusicItem>(MUSIC_TAG)

        if (musicItem != null) {
            viewModel.initExistingData(musicItem)
            bindMusicData(musicItem)
            currentData = musicItem
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            userId = user.userId


            viewModel.isBookmarked(currentData?.id.toString(), userId!!).observe(this) {
                isBookmarked = it
                if (isBookmarked) {
                    binding.bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled_24px)
                } else {
                    binding.bookmarkButton.setImageResource(R.drawable.ic_bookmark_24px)
                }
            }
        }

        // Enable shared element transition
        supportPostponeEnterTransition()
        setupAction()
        setupViewModelObserve()

        // Start the transition after binding data
        binding.root.post { supportStartPostponedEnterTransition() }

    }

    private fun setupAction() {
        binding.playButton.setOnClickListener {
            if (isInternetAvailable()) {
                // Internet is available, proceed with the intent
                val intent = Intent(this, MusicPlayerActivity::class.java)
                intent.putExtra(MUSIC_TAG, currentData)
                startActivity(intent)
            } else {
                // Show alert dialog if internet is not available
                showNoInternetDialog()
            }
        }

        binding.returnButton.setOnClickListener {
            finish()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.fetchDetailData(currentData?.id.toString())
        }

        binding.bookmarkButton.setOnClickListener {
            toggleBookmark(binding.bookmarkButton)
        }
    }

    // Function to check internet availability
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Function to show an alert dialog
    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Internet connection is mandatory to play music. Please connect to the internet and try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog when the user clicks OK
            }
            .setCancelable(false)
            .show()
    }

    private fun bindMusicData(musicItem: MusicItem) {
        // Bind data to the views
        binding.titleText.text = musicItem.name
        binding.summaryText.text = musicItem.musicDescription
        binding.authorText.text = musicItem.author
        binding.difficultyText.text = musicItem.difficulty
    }

    private fun toggleBookmark(button: ImageButton) {
        // Animate the button with a scaling effect
        val scaleUpX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.2f)
        val scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.2f)
        val scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", 1.2f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1.2f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleUpX).with(scaleUpY)
        animatorSet.play(scaleDownX).after(scaleUpX)
        animatorSet.play(scaleDownY).after(scaleUpY)

        // Toggle bookmark state
        isBookmarked = !isBookmarked
        if (isBookmarked) {
            button.setImageResource(R.drawable.ic_bookmark_filled_24px)

            userId?.let {
                currentData?.apply {
                    viewModel.addBookmark(MusicEntity(
                        userId = it,
                        difficulty = difficulty,
                        author = author,
                        name = name ?: "",
                        musicDescription = musicDescription,
                        id = id.toString())
                    )
                }
            }
        } else {
            button.setImageResource(R.drawable.ic_bookmark_24px)
            viewModel.removeBookmark(currentData?.id.toString())
        }

        // Start the animation
        animatorSet.duration = 300
        animatorSet.start()
    }

    private fun setupViewModelObserve() {
        viewModel.musicDetail.observe(this) { result ->
            when(result) {
                is ResultStatus.Error -> {
//                    showErrorRetry(true)
//                    setShimmerAnimation(false)
                }
                is ResultStatus.Loading -> {
//                    showErrorRetry(false)
//                    setShimmerAnimation(true)
                }
                is ResultStatus.Success -> {
//                    showErrorRetry(false)
//                    setShimmerAnimation(false)
                    val data = result.data
                    if (data.data != null) {
                        bindMusicData(data.data)
                        currentData = data.data
                    }
                }

                else -> {}
            }

        }



    }
//
//    private fun setShimmerAnimation(play: Boolean) {
//        val shimmerDetailLayout = binding.shimmerDetailLayout
//        val shimmerDetail = binding.shimmerDetail
//        val loadAnimation = AnimationUtils.loadAnimation(this, R.anim.shimmer_animation)
//
//        shimmerDetail.apply {
//            if (play) {
//                shimmerDetailLayout.visibility = View.VISIBLE
//                shimmerTvEventTitle.startAnimation(loadAnimation)
//                shimmerTvEventDescription.startAnimation(loadAnimation)
//                shimmerImgEventIcon.startAnimation(loadAnimation)
//            } else {
//                binding.shimmerDetailLayout.visibility = View.INVISIBLE
//                shimmerTvEventTitle.clearAnimation()
//                shimmerTvEventDescription.clearAnimation()
//                shimmerImgEventIcon.clearAnimation()
//            }
//        }
//    }
//
}