package com.lighthouse.lingo_swap

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.constriant.ViewType.Companion.findClassByItsName
import com.lighthouse.domain.response.ContentVO
import com.lighthouse.domain.response.ViewTypeVO
import java.lang.reflect.Type

class ViewTypeDeserializer : JsonDeserializer<ViewTypeVO> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ViewTypeVO {
        val jsonObject = json?.asJsonObject ?: throw IllegalArgumentException("Json Parsing 실패")
        Log.d("MYTAG", jsonObject.toString())
        val viewTypeString = jsonObject["viewType"].asString
        val viewType: ViewType = findClassByItsName(viewTypeString)
        val content = jsonObject["content"].asJsonObject
        val contentVO: Type = ViewType.findViewTypeClassByItsName(viewTypeString)
        val deserialize: ContentVO = Gson().fromJson(content, contentVO)

        return ViewTypeVO(viewType, deserialize)
    }
}