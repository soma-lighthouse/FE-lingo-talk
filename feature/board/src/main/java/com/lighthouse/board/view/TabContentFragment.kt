package com.lighthouse.board.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.QuestionTileBinding
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.board.R
import com.lighthouse.board.adapter.makeAdapter
import com.lighthouse.board.databinding.FragmentTabContentBinding
import com.lighthouse.board.viewmodel.BoardViewModel
import com.lighthouse.domain.request.UpdateLikeVO
import com.lighthouse.domain.response.vo.BoardQuestionVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TabContentFragment :
    BindingFragment<FragmentTabContentBinding>(R.layout.fragment_tab_content) {
    private val viewModel: BoardViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private lateinit var adapter: SimpleListAdapter<BoardQuestionVO, QuestionTileBinding>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initRefresh()

        val curPos = arguments?.getInt("tab_pos") ?: "Default Content"
//        val order = arguments?.getString("order") ?: "date"

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchState(curPos as Int, null).collect {
                    render(it)
                }
            }
        }
    }

    private fun initRefresh() {
        binding.srBoard.setOnRefreshListener {
            Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show()
            binding.srBoard.isRefreshing = false
        }
    }

    private fun initAdapter() {
        adapter = makeAdapter({ questionId, memberId ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.updateLike(questionId, UpdateLikeVO(memberId)).collect {
                        Log.d("UPDATE", it.toString())
                    }
                }
            }
        }, { userId ->
            mainNavigator.navigateToProfile(
                requireContext(),
                Pair("userId", userId),
                Pair("isMe", false)
            )
        })

        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvQuestion.layoutManager = linearLayoutManager
        binding.rvQuestion.adapter = adapter
    }


    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbBoardLoading.setVisible()
                binding.srBoard.setGone()

            }

            is UiState.Success<*> -> {
                binding.srBoard.setVisible()
                adapter.submitList(uiState.data as List<BoardQuestionVO>)
                binding.pbBoardLoading.setGone()
            }

            is UiState.Error -> {
                context.toast(uiState.message)
                binding.pbBoardLoading.setGone()
            }
        }
    }

}