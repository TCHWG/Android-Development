package com.kuroakevizago.dicodingstoryapp.view.customview.auth

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat.getString
import com.kuroakevizago.aira.R

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var isPasswordValid = false

    init {
        setup()
    }

    private fun setup() {
        // Set default input type as password
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isPasswordValid = validatePassword(s)
            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    private fun validatePassword(password: CharSequence?): Boolean {
        // Regex to check for at least 8 characters, one letter, one number, and one special character
        val isValid = password != null && password.matches(
            Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")
        )


        error = if (!isValid && !password.isNullOrEmpty()) {
            context.getString(R.string.please_enter_your_password)
        } else null

        return isValid
    }

}