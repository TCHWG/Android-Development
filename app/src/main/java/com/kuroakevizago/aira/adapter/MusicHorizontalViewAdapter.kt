package com.kuroakevizago.aira.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.databinding.CardViewSmallBinding
import com.kuroakevizago.aira.databinding.CardViewSmallShimmerBinding
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.ui.detail.DetailActivity
import com.kuroakevizago.aira.utils.Tags.Companion.MUSIC_TAG

class MusicHorizontalViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var musicList: List<MusicItem?> = emptyList()
    var dataResultStatus: ResultStatus<List<MusicItem?>> = ResultStatus.Loading

    inner class ViewHolder(private val viewBinding: CardViewSmallBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(music: MusicItem) {
            viewBinding.cardTitleText.text = music.name
            viewBinding.cardDescriptionText.text = music.musicDescription
            viewBinding.cardFooterText.text = music.author
            viewBinding.cardIndicatorText.text = music.difficulty
            when (music.difficulty?.lowercase()) {
                "easy" -> {
                    viewBinding.cardIndicatorText.backgroundTintList =
                        ContextCompat.getColorStateList(
                            viewBinding.root.context,
                            R.color.md_theme_success
                        )
                }

                "warning" -> {
                    viewBinding.cardIndicatorText.backgroundTintList =
                        ContextCompat.getColorStateList(
                            viewBinding.root.context,
                            R.color.md_theme_warning
                        )
                }

                "hard" -> {
                    viewBinding.cardIndicatorText.backgroundTintList =
                        ContextCompat.getColorStateList(
                            viewBinding.root.context,
                            R.color.md_theme_error
                        )
                }
            }


            itemView.setOnClickListener {

                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(MUSIC_TAG, music)

                val activity = itemView.context as Activity
                activity.startActivity(intent)
                @Suppress("DEPRECATION")
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    inner class ShimmerViewHolder(private val viewBinding: CardViewSmallShimmerBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun startShimmer() {
            // Apply shimmer animation to the views
            viewBinding.shimmerCardName.startAnimation(
                AnimationUtils.loadAnimation(
                    itemView.context,
                    R.anim.shimmer_animation
                )
            )
            viewBinding.shimmerCardImg.startAnimation(
                AnimationUtils.loadAnimation(
                    itemView.context,
                    R.anim.shimmer_animation
                )
            )
        }

        fun stopShimmer() {
            viewBinding.shimmerCardName.clearAnimation()
            viewBinding.shimmerCardImg.clearAnimation()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (dataResultStatus) {
            is ResultStatus.Success -> {
                val cardViewBinding =
                    CardViewSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ViewHolder(cardViewBinding)
            }
            else -> {
                val shimmerBinding = CardViewSmallShimmerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ShimmerViewHolder(shimmerBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(dataResultStatus) {
            is ResultStatus.Success -> {
                if (holder is ViewHolder) {
                    val storyData = musicList[position]
                    if (storyData != null)
                        holder.bind(storyData)

                } else if (holder is ShimmerViewHolder) {
                    holder.stopShimmer()
                }
            }
            else -> {
                if (holder is ShimmerViewHolder) {
                    holder.startShimmer()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (musicList.isEmpty() && dataResultStatus is ResultStatus.Loading) 2 else musicList.size
    }

}