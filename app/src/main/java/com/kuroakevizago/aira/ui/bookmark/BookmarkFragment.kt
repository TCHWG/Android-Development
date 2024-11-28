package com.kuroakevizago.aira.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kuroakevizago.aira.databinding.FragmentHomeBinding
import com.kuroakevizago.aira.ui.ViewModelFactory

class BookmarkFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun setupDataObserve() {
//
//        // Initialize the RecyclerView
//        val adapter = StoriesViewAdapter()
//        binding.recycleViewStories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        binding.recycleViewStories.adapter = adapter
//
//        // Observe the stories data
//        viewModel.musics.observe(this) { result ->
//            when (result) {
//                is ResultStatus.Loading -> {
//                    showErrorRetry(false)
//                    adapter.dataResultStatus = ResultStatus.Loading
//                }
//
//                is ResultStatus.Success -> {
//                    showErrorRetry(false)
//                    adapter.storyList = result.data.data
//                    adapter.dataResultStatus = ResultStatus.Success(result.data.data)
//                }
//
//                is ResultStatus.Error -> {
//                    adapter.dataResultStatus = result
//                    showErrorRetry(true)
//                    binding.errorRetry.errorDescription.text = result.error
//                }
//            }
//            binding.recycleViewStories.adapter = adapter
//            adapter.notifyDataSetChanged()
//        }
//    }
}