package com.kuroakevizago.aira.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.kuroakevizago.aira.R

@SuppressLint("ClickableViewAccessibility")
fun View.addTouchScaleEffect(
    scaleFactor: Float = 0.85f,
    duration: Long = 100,
) {
    val originalBackground = this.background // Save the original background to restore later


    this.setOnTouchListener { v, event ->

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.animate().scaleX(scaleFactor).scaleY(scaleFactor).setDuration(duration).start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
                if (originalBackground != null) {
                    v.background = originalBackground // Restore the original background
                }
            }
        }
        false
    }
}

fun View.setEnabledWithTransparency(isEnabled: Boolean, disabledAlpha: Float = 0.5f) {
    this.isEnabled = isEnabled
    this.alpha = if (isEnabled) 1f else disabledAlpha
}