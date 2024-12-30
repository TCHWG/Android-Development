package com.kuroakevizago.aira.ui.music_list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kuroakevizago.aira.adapter.MusicVerticalViewAdapter
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.data.status.ResultStatus

import com.kuroakevizago.aira.databinding.FragmentMusicsListBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.main.MainViewModel

class MusicListFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var binding: FragmentMusicsListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMusicsListBinding.inflate(inflater, container, false)


        if (!viewModel.isMusicsFetched)
            viewModel.fetchFeaturedMusics()

        setupDataAction()
        setupDataObserve()

        return binding.root
    }


    private fun setupDataAction() {
        binding.allMusicsRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
            showFeaturedMusicsErrorRetry(false)
            viewModel.fetchFeaturedMusics()
        }

        binding.allMusicsRecyclerView.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchFeaturedMusics()
        }
    }

    private fun setupDataObserve() {
        setupFeaturedMusicsData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupFeaturedMusicsData() {

        // Initialize the RecyclerView
        val adapter = MusicVerticalViewAdapter()
        val featuredRecyclerView = binding.allMusicsRecyclerView.recycleView
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
                        binding.allMusicsRecyclerView.textNoDataObtain
                    )
                    binding.allMusicsRecyclerView.swipeRefreshLayout.isRefreshing = false
                }

                is ResultStatus.Error -> {
                    adapter.dataResultStatus = result
                    showFeaturedMusicsErrorRetry(true)
                    binding.allMusicsRecyclerView.errorRetry.errorDescription.text = result.error
                    binding.allMusicsRecyclerView.swipeRefreshLayout.isRefreshing = false
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
        binding.allMusicsRecyclerView.recycleView.visibility =
            if (show) View.INVISIBLE else View.VISIBLE
        binding.allMusicsRecyclerView.errorRetry.root.visibility =
            if (show) View.VISIBLE else View.GONE
        if (show) {
            binding.allMusicsRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
                showFeaturedMusicsErrorRetry(false)
                viewModel.fetchFeaturedMusics()
            }
        }
    }
}