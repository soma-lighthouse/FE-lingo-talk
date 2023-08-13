package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.BoardVO

data class BoardDTO(
    @SerializedName("questions")
    val questions: List<BoardQuestionDTO>?,
) {
    fun toVO() = BoardVO(
        questions = questions?.map { it.toVO() } ?: listOf()
    )
}