package com.kuroakevizago.aira.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kuroakevizago.aira.adapter.EvaluationVerticalViewAdapter
import com.kuroakevizago.aira.adapter.MusicVerticalViewAdapter
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.data.remote.response.UserMusics
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.databinding.FragmentHomeBinding
import com.kuroakevizago.aira.databinding.FragmentProfileBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.auth.login.LoginActivity
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

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                viewModel.logout()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }

            if (!viewModel.isEvaluationsFetched)
                viewModel.fetchUserEvaluations(user.userId)
        }

        setupDataAction()
        setupDataObserve()

        return binding.root
    }


    private fun setupDataAction() {

        binding.evaluationHistoryRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
            viewModel.userId?.let { it1 ->
                showEvaluationsErrorRetry(false)
                viewModel.fetchUserEvaluations(it1)
            }
        }

        binding.refreshButton.setOnClickListener {
            viewModel.userId?.let { it1 -> viewModel.fetchUserEvaluations(it1) }
            viewModel.fetchUserProfile()
        }
    }

    private fun setupDataObserve() {
//        setupFeaturedMusicsData()
        setupHistoryData()
        setupUserData()
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setupHistoryData() {

        // Initialize the RecyclerView
        val adapter = EvaluationVerticalViewAdapter()
        val historyRecyclerView = binding.evaluationHistoryRecyclerView.recycleView
        historyRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
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
                    adapter.userMusics = sortedUserMusics
                    adapter.dataResultStatus = ResultStatus.Success(sortedUserMusics)

                    checkRecyclerData(
                        adapter.userMusics,
                        historyRecyclerView,
                        binding.evaluationHistoryRecyclerView.textNoDataObtain
                    )

                    binding.evaluationsHistoryText.text = "${adapter.userMusics.count()} evaluated musics"
                }

                is ResultStatus.Error -> {
                    adapter.dataResultStatus = result
                    showEvaluationsErrorRetry(true)
                    binding.evaluationHistoryRecyclerView.errorRetry.errorDescription.text = result.error
                }
            }
            historyRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }


    private fun checkRecyclerData(list: List<UserMusics?>, recyclerView: RecyclerView, errorText: TextView) {
        if (list.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            errorText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            errorText.visibility = View.INVISIBLE
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