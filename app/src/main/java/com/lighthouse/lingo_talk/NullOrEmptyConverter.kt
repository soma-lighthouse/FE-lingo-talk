package com.lighthouse.lingo_talk

import com.lighthouse.android.data.model.response.EmptyResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class NullOrEmptyConverter : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *> {
        val delegate = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
        return Converter<ResponseBody, Any> {
            if (it.contentLength() == 0L) return@Converter EmptyResponse()
            delegate.convert(it)
        }
    }

}