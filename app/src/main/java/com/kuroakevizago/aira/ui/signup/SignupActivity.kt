package com.kuroakevizago.aira.ui.signup

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.databinding.ActivitySignupBinding
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.login.LoginViewModel

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivitySignupBinding

    private lateinit var auth: FirebaseAuth

    private var isNameValid: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        setupAction()
    }

    private fun setupAction() {

        signupDataCheck()

        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText
        val nameEditText = binding.nameEditText

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isNameValid = nameEditText.validateNotEmptyText()
                signupDataCheck()
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })

        passwordEditText.addTextChangedListener { signupDataCheck() }
        emailEditText.addTextChangedListener { signupDataCheck() }

        binding.signupButton.setOnClickListener {
            viewModel.register(
                nameEditText.text.toString(),
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            ).observe(this) {
                when (it) {
                    is ResultStatus.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.failed))
                            setMessage("${getString(R.string.something_went_wrong)}\n\n${it.error}")
                            setNegativeButton(getString(R.string.close)) { _, _ ->
                            }
                            create()
                            show()
                        }
                    }
                    is ResultStatus.Loading -> {
                        showLoading(true)
                    }
                    is ResultStatus.Success -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.success))
                            setMessage("${getString(R.string.account_created)} ${it.data.message}")
                            setPositiveButton(getString(R.string.next)) { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.circularProgress.root.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun signupDataCheck() {
        binding.signupButton.isEnabled =
            binding.emailEditText.isEmailValid && binding.passwordEditText.isPasswordValid && isNameValid
    }


    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }
        // [END create_user_with_email]
    }
}