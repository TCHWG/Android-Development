package com.kuroakevizago.aira.ui.bookmark

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
import com.kuroakevizago.aira.databinding.FragmentBookmarkBinding
import com.kuroakevizago.aira.ui.ViewModelFactory
import com.kuroakevizago.aira.ui.main.MainViewModel

class BookmarkFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var binding: FragmentBookmarkBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        setupDataAction()
        setupDataObserve()

        return binding.root
    }


    private fun setupDataAction() {

        binding.bookmarkRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
            showFeaturedMusicsErrorRetry(false)
            viewModel.fetchFeaturedMusics()
        }

        binding.refreshButton.setOnClickListener {
            setupBookmarkedMusics()
        }
    }

    private fun setupDataObserve() {
        setupUserSessionData()
    }

    private fun setupUserSessionData() {
        viewModel.getSession().observe(viewLifecycleOwner) {user ->
            viewModel.userId = user.userId
            setupBookmarkedMusics()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setupBookmarkedMusics() {

        // Initialize the RecyclerView
        val adapter = MusicVerticalViewAdapter()
        val featuredRecyclerView = binding.bookmarkRecyclerView.recycleView
        featuredRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false)
        featuredRecyclerView.adapter = adapter

        // Observe the stories data
        viewModel.getBookmarkedMusics(viewModel.userId!!)?.observe(viewLifecycleOwner) {result ->
            showFeaturedMusicsErrorRetry(false)
            adapter.musicList = result.map { entity ->
                MusicItem(
                    id = entity.id.toInt(),
                    name = entity.name,
                    author = entity.author,
                    difficulty = entity.difficulty,
                    musicDescription = entity.musicDescription
                )
            }
            adapter.dataResultStatus = ResultStatus.Loading

            checkRecyclerData(
                adapter.musicList,
                featuredRecyclerView,
                binding.bookmarkRecyclerView.textNoDataObtain
            )

            adapter.dataResultStatus = ResultStatus.Success(adapter.musicList)
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
        binding.bookmarkRecyclerView.recycleView.visibility = if (show) View.INVISIBLE else View.VISIBLE
        binding.bookmarkRecyclerView.errorRetry.root.visibility = if (show) View.VISIBLE else View.GONE
        binding.bookmarkRecyclerView.textNoDataObtain.visibility = View.INVISIBLE
        if (show) {
            binding.bookmarkRecyclerView.errorRetry.btnRetryConnection.setOnClickListener {
                showFeaturedMusicsErrorRetry(false)
                viewModel.fetchFeaturedMusics()
            }
        }
    }


}