package com.lighthouse.android.data.repository

import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.domain.repository.DrivenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DrivenRepositoryImpl @Inject constructor(
    private val drivenRemoteDataSource: DrivenRemoteDataSource,
) : DrivenRepository {
    //    override fun getDriven(): Flow<List<ViewTypeVO>> =
//        drivenRemoteDataSource.getDriven()
    override fun getDriven(): Flow<Boolean> =
        drivenRemoteDataSource.getDriven()
}