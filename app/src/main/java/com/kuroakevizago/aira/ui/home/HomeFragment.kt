package com.kuroakevizago.aira.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.kuroakevizago.aira.R
import com.kuroakevizago.aira.adapter.MusicVerticalViewAdapter
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.databinding.FragmentHomeBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.main.MainViewModel

class HomeFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!viewModel.isMusicsFetched)
            viewModel.fetchFeaturedMusics()

        if (!viewModel.isPreviouslyPlayedFetched)
            viewModel.fetchPreviouslyPlayedMusics()

        if (!viewModel.isUserDataFetched)
            viewModel.fetchUserProfile()

        setupDataAction()
        setupDataObserve()

        return root
    }

    private fun setupDataAction() {
        binding.featuredMusicsRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
            showFeaturedMusicsErrorRetry(false)
            viewModel.fetchFeaturedMusics()
        }

        binding.previouslyPlayedRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
            showFeaturedMusicsErrorRetry(false)
            viewModel.fetchFeaturedMusics()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.fetchFeaturedMusics()
            viewModel.fetchPreviouslyPlayedMusics()
            viewModel.fetchUserProfile()
        }
    }

    private fun setupDataObserve() {
        setupFeaturedMusicsData()
        setupPreviouslyPlayedData()
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

    @SuppressLint("NotifyDataSetChanged")
    private fun setupFeaturedMusicsData() {

        // Initialize the RecyclerView
        val adapter = MusicVerticalViewAdapter()
        val featuredRecyclerView = binding.featuredMusicsRecyclerView.recycleView
        featuredRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false)
        featuredRecyclerView.adapter = adapter

        // Observe the stories data
        viewModel.musics.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultStatus.Loading -> {
                    showFeaturedMusicsErrorRetry(false)
                    adapter.dataResultStatus = ResultStatus.Loading
                }

                is ResultStatus.Success -> {
                    showFeaturedMusicsErrorRetry(false)
                    adapter.musicList = result.data.data?.take(5) ?: emptyList()
                    adapter.dataResultStatus = ResultStatus.Success(result.data.data)
                    checkRecyclerData(
                        adapter.musicList,
                        featuredRecyclerView,
                        binding.featuredMusicsRecyclerView.textNoDataObtain
                    )
                }

                is ResultStatus.Error -> {
                    adapter.dataResultStatus = result
                    showFeaturedMusicsErrorRetry(true)
                    binding.featuredMusicsRecyclerView.errorRetry.errorDescription.text = result.error
                }
            }
            featuredRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun checkRecyclerData(list: List<MusicItem?>, recyclerView: RecyclerView, errorText: TextView) {
        if (list.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            errorText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            errorText.visibility = View.INVISIBLE
        }
    }

    private fun showFeaturedMusicsErrorRetry(show: Boolean) {
        binding.featuredMusicsRecyclerView.recycleView.visibility = if (show) View.INVISIBLE else View.VISIBLE
        binding.featuredMusicsRecyclerView.errorRetry.root.visibility = if (show) View.VISIBLE else View.GONE
        binding.featuredMusicsRecyclerView.textNoDataObtain.visibility = View.INVISIBLE
        if (show) {
            binding.featuredMusicsRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
                showFeaturedMusicsErrorRetry(false)
                viewModel.fetchFeaturedMusics()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupPreviouslyPlayedData() {

        // Initialize the RecyclerView
        val adapter = MusicVerticalViewAdapter()
        val previouslyPlayedRecyclerView = binding.previouslyPlayedRecyclerView.recycleView
        previouslyPlayedRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false)
        previouslyPlayedRecyclerView.adapter = adapter

        // Observe the data
        viewModel.previouslyPlayed.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultStatus.Loading -> {
                    showPreviouslyPlayedErrorRetry(false)
                    adapter.dataResultStatus = ResultStatus.Loading
                }

                is ResultStatus.Success -> {
                    showPreviouslyPlayedErrorRetry(false)
                    adapter.musicList = result.data.data ?: emptyList()
                    adapter.dataResultStatus = ResultStatus.Success(result.data.data)
                    checkRecyclerData(
                        adapter.musicList,
                        previouslyPlayedRecyclerView,
                        binding.previouslyPlayedRecyclerView.textNoDataObtain
                    )
                }

                is ResultStatus.Error -> {
                    adapter.dataResultStatus = result
                    showPreviouslyPlayedErrorRetry(true)
                    binding.previouslyPlayedRecyclerView.errorRetry.errorDescription.text = result.error

                    if (result.error.lowercase().contains("invalid token")) {
                        viewModel.logout()
                    }
                }
            }
            previouslyPlayedRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun showPreviouslyPlayedErrorRetry(show: Boolean) {
        binding.previouslyPlayedRecyclerView.recycleView.visibility = if (show) View.INVISIBLE else View.VISIBLE
        binding.previouslyPlayedRecyclerView.errorRetry.root.visibility = if (show) View.VISIBLE else View.GONE
        binding.previouslyPlayedRecyclerView.textNoDataObtain.visibility = View.INVISIBLE
        if (show) {
            binding.previouslyPlayedRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
                showPreviouslyPlayedErrorRetry(false)
                viewModel.fetchPreviouslyPlayedMusics()
            }
        }
    }
}