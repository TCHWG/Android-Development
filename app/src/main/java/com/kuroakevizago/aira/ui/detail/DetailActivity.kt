package com.kuroakevizago.aira.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.adapter.MusicVerticalViewAdapter
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.databinding.ActivityDetailBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.login.LoginActivity
import com.kuroakevizago.aira.data.status.ResultStatus

class DetailActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityDetailBinding

    private var detailId: String = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        setupViewModelObserve()
    }

    @Suppress("DEPRECATION")
    private fun setupViewModelObserve() {
        val intent = this.intent
        val musicItem = intent.getParcelableExtra<MusicItem>(MusicVerticalViewAdapter.MUSIC_TAG)
//
//        if (detailId != "" && !viewModel.isIdValid(detailId) && !viewModel.isFetched) {
//            viewModel.fetchStory(detailId)
//        }
//
//        viewModel.story.observe(this) { result ->
//            when(result) {
//                is ResultStatus.Error -> {
//                    showErrorRetry(true)
//                    setShimmerAnimation(false)
//                }
//                is ResultStatus.Loading -> {
//                    showErrorRetry(false)
//                    setShimmerAnimation(true)
//                }
//                is ResultStatus.Success -> {
//                    showErrorRetry(false)
//                    setShimmerAnimation(false)
//                    val data = result.data.story
//                    if (data != null) {
//                        setDetail(data.name, data.description, data.photoUrl)
//                    } else {
//                        showErrorRetry(true)
//                    }
//                }
//            }
//
//        }

    }

    private fun setDetail(title: String?, description: String?, imageUrl: String?) {
        binding.textTitle.text = title
        binding.textDescription.text = description
        Glide.with(binding.root).load(imageUrl).into(binding.imageDetail)
    }

    private fun setShimmerAnimation(play: Boolean) {
        val shimmerDetailLayout = binding.shimmerDetailLayout
        val shimmerDetail = binding.shimmerDetail
        val loadAnimation = AnimationUtils.loadAnimation(this, R.anim.shimmer_animation)

        shimmerDetail.apply {
            if (play) {
                shimmerDetailLayout.visibility = View.VISIBLE
                shimmerTvEventTitle.startAnimation(loadAnimation)
                shimmerTvEventDescription.startAnimation(loadAnimation)
                shimmerImgEventIcon.startAnimation(loadAnimation)
            } else {
                binding.shimmerDetailLayout.visibility = View.INVISIBLE
                shimmerTvEventTitle.clearAnimation()
                shimmerTvEventDescription.clearAnimation()
                shimmerImgEventIcon.clearAnimation()
            }
        }
    }
//
//    private fun showErrorRetry(show: Boolean) {
//        binding.detailLayout.visibility = if (show) View.INVISIBLE else View.VISIBLE
//        binding.errorDetailLayout.visibility = if (show) View.VISIBLE else View.GONE
//        if (show) {
//            binding.errorDetailRetry.btnRetryConnection.setOnClickListener {
//                showErrorRetry(false)
//                viewModel.fetchStory(detailId)
//            }
//        }
//    }
}