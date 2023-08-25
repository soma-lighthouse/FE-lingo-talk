package com.lighthouse.domain.usecase

import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.repository.AuthRepository
import java.util.UUID
import javax.inject.Inject

class GetAuthUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    fun getUserId() = repository.getUserId()

    fun saveUserId() {
        val uuid = UUID.randomUUID()
        repository.saveUserId(uuid.toString())
    }

    fun getInterestList() = repository.getInterestList()

    fun getLanguageList() = repository.getLanguageList()

    fun getCountryList() = repository.getCountryList()

    fun registerUser(info: RegisterInfoVO) = repository.registerUser(info)

    fun getPreSignedURL(fileName: String) = repository.getPreSignedURL(fileName)

    fun uploadImg(url: String, profilePath: String) = repository.uploadImg(url, profilePath)
}