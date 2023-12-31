package com.lighthouse.android.chats.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.logging.ChatQuestionInteractLogger
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.ChatRepository
import com.lighthouse.swm_logging.SWMLogging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    dispatcherProvider: DispatcherProvider,
    application: Application
) : BaseViewModel(dispatcherProvider, application) {
    private val _question = MutableStateFlow<UiState>(UiState.Loading)
    val question = _question.asStateFlow()
    private val _questionList: MutableMap<Int, MutableList<String>> = mutableMapOf()

    val position = MutableLiveData(1)

    val sendQuestion: MutableLiveData<String> = MutableLiveData()
    var next: MutableList<Int?> = MutableList(7) { null }

    fun getQuestion() {
        val category = position.value!!
        if (next[category] == -1) {
            return
        }
        onIO {
            chatRepository.getRecommendedQuestions(category, next[category])
                .onStart {
                    _question.value = UiState.Loading
                }
                .catch {
                    _question.value = handleException(it)
                }.collect {
                    if (it.nextId == -1) {
                        next[category] = -1
                    } else {
                        next[category] = it.nextId
                    }
                    val questions = it.questions
                    if (_questionList[category] == null) {
                        _questionList[category] = mutableListOf()
                    }
                    _questionList[category]?.addAll(questions)
                    _question.value = UiState.Success(_questionList[category] ?: mutableListOf())
                }
        }
    }

    private fun getQuestionInteractLogging(
        stayTime: Double,
        question: String,
        position: Int
    ): ChatQuestionInteractLogger {
        return ChatQuestionInteractLogger.Builder()
            .setUuid(authRepository.getUserId() ?: "")
            .setStayTime(stayTime)
            .setQuestion(question)
            .setCategory(position)
            .build()
    }

    fun sendQuestionInteractLogging(stayTime: Double, question: String) {
        val scheme = getQuestionInteractLogging(stayTime, question, position.value!!)
        SWMLogging.logEvent(scheme)
    }

    fun getQuestionList() = _questionList[position.value!!]
}