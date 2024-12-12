package com.kuroakevizago.aira.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.databinding.ActivityLoginBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.main.MainActivity
import com.kuroakevizago.aira.ui.auth.signup.SignupActivity
import com.kuroakevizago.aira.utils.addTouchScaleEffect
import com.kuroakevizago.aira.data.pref.UserModel
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModelObserver()

        // Initialize Firebase Auth
        auth = Firebase.auth
        setupAction()
    }

    private fun setupViewModelObserver() {
        val email = binding.emailEditText.text.toString()

        viewModel.loginStatus.observe(this) { result ->
            when (result) {
                is ResultStatus.Error -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.failed))
                        setMessage("${getString(R.string.something_went_wrong)}\n${result.error}")
                        setNegativeButton(getString(R.string.close)) { _, _ -> }
                        create()
                        show()
                    }
                }

                is ResultStatus.Loading -> {
                    showLoading(true)
                }

                is ResultStatus.Success -> {
                    showLoading(false)
                    val resultData = result.data.data
                    viewModel.saveSession(UserModel(resultData?.user?.uid.toString(), email, resultData?.token!!))
                    moveToMainActivity()
                }
            }
        }
    }

    private fun setupAction() {
        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText
        loginDataCheck()

        binding.emailEditText.addTextChangedListener { loginDataCheck() }
        binding.passwordEditText.addTextChangedListener { loginDataCheck() }

        binding.loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            viewModel.login(email, password)
        }


        binding.signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.signInGoogleButton.setOnClickListener {
            signIn()
        }

        binding.loginButton.addTouchScaleEffect()
        binding.signupButton.addTouchScaleEffect()
        binding.signInGoogleButton.addTouchScaleEffect()

    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loginDataCheck() {
        binding.loginButton.isEnabled =
            binding.emailEditText.isEmailValid && binding.passwordEditText.isPasswordValid
    }

    private fun showLoading(show: Boolean) {
        binding.circularProgress.root.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun signIn() {
        val credentialManager = CredentialManager.create(this) //import from androidx.CredentialManager

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id)) //from https://console.firebase.google.com/project/firebaseProjectName/authentication/providers
            .build()

        val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                showLoading(true) // Show the progress bar
                val result: GetCredentialResponse = credentialManager.getCredential(
                    //import from androidx.CredentialManager
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result) // Handle the successful sign-in result
            } catch (e: GetCredentialException) { // Import from androidx.CredentialManager
                Log.d("Error", e.message.toString()) // Log the error message
            } finally {
                showLoading(false) // Always hide the progress bar, whether successful or not
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        showLoading(true)
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        showLoading(false)

                        // Use googleIdTokenCredential and extract id to validate and authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser

                    user?.getIdToken(true)?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                val token: String? = it.result.token
                                if (token != null) {
                                    viewModel.saveSession(UserModel(user.uid, user.email!!, token))
                                    moveToMainActivity()
                                } else {
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.failed))
                                        setMessage("${getString(R.string.something_went_wrong)}\n Try again")
                                        setNegativeButton(getString(R.string.close)) { _, _ -> }
                                        create()
                                        show()
                                    }
                                }
                            } else {
                                // Handle error -> task.getException();
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

}