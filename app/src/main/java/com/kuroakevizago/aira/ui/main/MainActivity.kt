package com.kuroakevizago.aira.ui.main

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.databinding.ActivityMainBinding
import com.kuroakevizago.aira.databinding.SearchBarBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.auth.login.LoginActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, true)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private var isSearchBarVisible = true
    private lateinit var searchBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // User Session checking
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        searchBar = binding.searchBar.root

        setupNavView()
        setupAction()
    }

    private fun setupNavView() {

        // Bottom nav view setup
        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        val navController = navHostFragment!!.findNavController()
        navView.setupWithNavController(navController)

        // Listen for destination changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_profile -> {
                    // Disable or hide the search bar if on the profile menu
                    if (isSearchBarVisible) {
                        slideSearchBar(false)
                    }
                }
                else -> {
                    // Enable or show the search bar if not on the profile menu
                    if (!isSearchBarVisible) {
                        slideSearchBar(true)

                    }
                }
            }
        }
    }

    private fun setupAction() {
//
//        binding.buttonLogout.setOnClickListener {
//            AlertDialog.Builder(this).apply {
//                setTitle(getString(R.string.logout))
//                setMessage(getString(R.string.are_you_sure_to_logout))
//                setPositiveButton(getString(R.string.sure)) {_,_ ->
//                    signOut()
//                }
//                setNegativeButton(getString(R.string.close)) { _, _ ->
//                }
//                create()
//                show()
//            }
//        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun openLanguageSettings(context: Context) {
        val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                this,
                getString(R.string.language_settings_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun signOut() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(this@MainActivity)
            viewModel.logout()
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun slideSearchBar(isVisible: Boolean) {
        if (isVisible) {
            // Slide in (show)
            searchBar.visibility = View.VISIBLE
            isSearchBarVisible = true
            val slideInAnimator = ObjectAnimator.ofFloat(searchBar, "translationY", -searchBar.height.toFloat(), 0f)
            slideInAnimator.duration = 300
            slideInAnimator.start()
        } else {
            // Slide out (hide)
            val slideOutAnimator = ObjectAnimator.ofFloat(searchBar, "translationY", 0f, -searchBar.height.toFloat())
            slideOutAnimator.duration = 300
            slideOutAnimator.start()
            slideOutAnimator.addListener(onEnd = {
                searchBar.visibility = View.GONE
                isSearchBarVisible = false
            })
        }
    }
}