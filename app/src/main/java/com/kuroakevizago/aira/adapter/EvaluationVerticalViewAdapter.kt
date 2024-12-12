@file:Suppress("DEPRECATION")

package com.kuroakevizago.aira.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.data.remote.response.UserMusics
import com.kuroakevizago.aira.databinding.CardViewSmallShimmerBinding
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.databinding.CardViewEvaluationBinding
import com.kuroakevizago.aira.ui.evaluation.EvaluationActivity
import com.kuroakevizago.aira.utils.Tags.Companion.EVALUATION_TAG
import com.kuroakevizago.aira.utils.Tags.Companion.MUSIC_TAG

class EvaluationVerticalViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var userMusics: List<UserMusics?> = emptyList()
    var dataResultStatus: ResultStatus<List<UserMusics?>?> = ResultStatus.Loading

    inner class ViewHolder(private val viewBinding: CardViewEvaluationBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(userMusicItem: UserMusics) {
            val musicEvaluation = userMusicItem.evaluations?.get(0)

            viewBinding.cardTitleText.text = "${userMusicItem.date} | ${userMusicItem.time}"
            viewBinding.cardDescriptionText.text = musicEvaluation?.description
            viewBinding.cardFooterText.text = userMusicItem.music?.name
            viewBinding.cardTypeText.text = "Mistake Type: ${musicEvaluation?.name}"

            itemView.setOnClickListener {

                val intent = Intent(itemView.context, EvaluationActivity::class.java)
                intent.putExtra(EVALUATION_TAG, userMusicItem)
                intent.putExtra(MUSIC_TAG, userMusicItem.music)

                val activity = itemView.context as Activity
                activity.startActivity(intent)
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
                    CardViewEvaluationBinding.inflate(
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
                    val data = userMusics[position]
                    if (data != null)
                        holder.bind(data)

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
        return if (userMusics.isEmpty() && dataResultStatus is ResultStatus.Loading) 2 else userMusics.size
    }

}