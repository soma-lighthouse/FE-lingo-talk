package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.repository.datasource.ProfileRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val datasource: ProfileRemoteDataSource,
    private val local: LocalPreferenceDataSource,
) : ProfileRepository {
    override fun getProfileDetail(userId: String): Flow<Resource<ProfileVO>> =
        datasource.getProfileDetail(userId)
            .map {
                when (it) {
                    is Resource.Success -> Resource.Success(it.data!!.toVO())
                    else -> Resource.Error(it.message ?: "No Message found")
                }
            }

    override fun getUUID() = local.getUUID()
}