package com.kuroakevizago.aira.ui.home

import android.annotation.SuppressLint
import android.content.Intent
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
import com.kuroakevizago.aira.adapter.EvaluationVerticalViewAdapter
import com.kuroakevizago.aira.adapter.MusicVerticalViewAdapter
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.data.remote.response.UserMusics
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.databinding.FragmentHomeBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.auth.login.LoginActivity
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

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                viewModel.logout()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }

            if (!viewModel.isEvaluationsFetched)
                viewModel.fetchUserEvaluations(user.userId)
        }

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

        binding.evaluationHistoryRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
            showFeaturedMusicsErrorRetry(false)
            viewModel.userId?.let { viewModel.fetchUserEvaluations(it) }
        }

        binding.homeRefreshLayout.setOnRefreshListener {
            viewModel.fetchFeaturedMusics()
            viewModel.userId?.let { viewModel.fetchUserEvaluations(it) }
            viewModel.fetchUserProfile()
        }

//
//        binding.featuredMusicsRecyclerView.swipeRefreshLayout.setOnRefreshListener {
//
//        }
//
//        binding.evaluationHistoryRecyclerView.swipeRefreshLayout.setOnRefreshListener {
//
//            viewModel.fetchUserProfile()
//        }
    }

    private fun setupDataObserve() {
        setupFeaturedMusicsData()
        setupHistoryData()

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
                    binding.homeRefreshLayout.isRefreshing = false
                    checkMusicRecyclerData(
                        adapter.musicList,
                        featuredRecyclerView,
                        binding.featuredMusicsRecyclerView.textNoDataObtain
                    )
                }

                is ResultStatus.Error -> {
                    adapter.dataResultStatus = result
                    showFeaturedMusicsErrorRetry(true)
                    binding.homeRefreshLayout.isRefreshing = false
                    binding.featuredMusicsRecyclerView.errorRetry.errorDescription.text = result.error
                }
            }
            featuredRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun checkMusicRecyclerData(list: List<MusicItem?>, recyclerView: RecyclerView, errorText: TextView) {
        if (list.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            errorText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            errorText.visibility = View.INVISIBLE
        }
    }

    private fun checkEvaluationsRecyclerData(list: List<UserMusics?>, recyclerView: RecyclerView, errorText: TextView) {
        if (list.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            errorText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            errorText.visibility = View.INVISIBLE
        }
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setupHistoryData() {

        // Initialize the RecyclerView
        val adapter = EvaluationVerticalViewAdapter()
        val historyRecyclerView = binding.evaluationHistoryRecyclerView.recycleView
        historyRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false)
        historyRecyclerView.adapter = adapter

        // Observe the stories data
        viewModel.userEvaluations.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultStatus.Loading -> {
                    showEvaluationsErrorRetry(false)
                    adapter.dataResultStatus = ResultStatus.Loading
                }

                is ResultStatus.Success -> {
                    val comparator = compareByDescending<UserMusics?> { it?.date }.thenByDescending { it?.time }
                    val sortedUserMusics = result.data.data?.sortedWith(comparator) ?: emptyList()

                    showEvaluationsErrorRetry(false)
                    adapter.userMusics = sortedUserMusics.take(5)
                    adapter.dataResultStatus = ResultStatus.Success(result.data.data)

                    binding.homeRefreshLayout.isRefreshing = false
                    checkEvaluationsRecyclerData(
                        adapter.userMusics,
                        historyRecyclerView,
                        binding.evaluationHistoryRecyclerView.textNoDataObtain
                    )
                }

                is ResultStatus.Error -> {
                    adapter.dataResultStatus = result
                    showEvaluationsErrorRetry(true)
                    binding.homeRefreshLayout.isRefreshing = false
                    binding.evaluationHistoryRecyclerView.errorRetry.errorDescription.text = result.error
                }
            }
            historyRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun showEvaluationsErrorRetry(show: Boolean) {
        val evaluationRecyclerView = binding.evaluationHistoryRecyclerView
        evaluationRecyclerView.recycleView.visibility = if (show) View.INVISIBLE else View.VISIBLE
        evaluationRecyclerView.errorRetry.root.visibility = if (show) View.VISIBLE else View.GONE
        evaluationRecyclerView.textNoDataObtain.visibility = View.INVISIBLE
        if (show) {
            evaluationRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
                showEvaluationsErrorRetry(false)
                viewModel.fetchFeaturedMusics()
            }
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

}