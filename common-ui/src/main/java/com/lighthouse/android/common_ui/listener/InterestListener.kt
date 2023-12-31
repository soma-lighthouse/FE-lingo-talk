package com.lighthouse.android.common_ui.listener

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.lighthouse.domain.entity.response.vo.InterestVO

interface InterestListener {
    val selectedInterest: MutableLiveData<List<InterestVO>>
    val collapse: ObservableInt
}