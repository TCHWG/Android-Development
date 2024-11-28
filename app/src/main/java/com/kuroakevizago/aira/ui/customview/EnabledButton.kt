package com.kuroakevizago.dicodingstoryapp.view.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.kuroakevizago.aira.R

class EnabledButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var txtColor: Int = 0
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable
    init {
        txtColor = ContextCompat.getColor(context, R.color.md_theme_onPrimaryContainer)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_enabled) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disabled) as Drawable
        isAllCaps = true
        setTextAppearance(R.style.Widget_AppTheme_Button)
    }

    @SuppressLint("SetTextI18n")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if(isEnabled) enabledBackground else disabledBackground
        setTextAppearance(R.style.Widget_AppTheme_Button)
        setTextColor(txtColor)
        gravity = Gravity.CENTER
        isAllCaps = true

    }
}