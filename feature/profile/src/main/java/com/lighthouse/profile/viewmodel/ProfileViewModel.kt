package com.lighthouse.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val useCase: GetProfileUseCase,
) : ViewModel() {
    fun getProfileDetail(userId: String) =
        useCase.getProfileDetail(userId)
            .map {
                when (it) {
                    is Resource.Success -> UiState.Success(it.data!!)
                    else -> UiState.Error(it.message ?: "Error found")
                }
            }
            .catch {
                emit(UiState.Error(it.message!!))
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = UiState.Loading,
                started = SharingStarted.WhileSubscribed(5000)
            )

    fun getUUID() = useCase.getUUID()
}