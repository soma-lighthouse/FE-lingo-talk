package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.MyQuestionsVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileDetail(uuid: String): Flow<Resource<ProfileVO>>

    fun getMyQuestions(): Flow<Resource<List<MyQuestionsVO>>>

    fun getUUID(): String?
}