package com.lighthouse.android.common_ui.base.selection_adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.databinding.CountryTileBinding
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.domain.entity.response.vo.CountryVO


class CountryViewHolder(private val binding: CountryTileBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: CountryVO) {
        binding.tvCountry.text = item.name

        ImageUtils.newInstance().setFlagImage(binding.ivFlag, item.code, binding.root.context)

        if (item.select) {
            binding.btnCheck.setVisible()
        } else {
            binding.btnCheck.setGone()
        }
    }
}