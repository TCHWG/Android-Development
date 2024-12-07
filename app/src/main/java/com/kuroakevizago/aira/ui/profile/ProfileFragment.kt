package com.kuroakevizago.aira.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.databinding.FragmentHomeBinding
import com.kuroakevizago.aira.databinding.FragmentProfileBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.main.MainViewModel

class ProfileFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupDataObserve()

        return binding.root
    }


    private fun setupDataAction() {
//        binding.featuredMusicsRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
//            showFeaturedMusicsErrorRetry(false)
//            viewModel.fetchFeaturedMusics()
//        }
//
//        binding.previouslyPlayedRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
//            showFeaturedMusicsErrorRetry(false)
//            viewModel.fetchFeaturedMusics()
//        }

        binding.refreshButton.setOnClickListener {
            viewModel.fetchFeaturedMusics()
            viewModel.fetchPreviouslyPlayedMusics()
            viewModel.fetchUserProfile()
        }
    }

    private fun setupDataObserve() {
//        setupFeaturedMusicsData()
//        setupPreviouslyPlayedData()
        setupUserData()
    }


    private fun setupUserData() {

        viewModel.userData.observe(viewLifecycleOwner) {result->
            when (result) {
                is ResultStatus.Error -> {

                }
                is ResultStatus.Loading -> {

                }
                is ResultStatus.Success -> {
                    val resultData = result.data.data

                    if (resultData != null) {
                        binding.userNameText.text = resultData.name

                        if (resultData.photoUrl != null)
                            Glide.with(requireContext()).load(resultData.photoUrl)
                                .into(binding.userPhotoImage)
                    }
                }
            }

        }
    }
}