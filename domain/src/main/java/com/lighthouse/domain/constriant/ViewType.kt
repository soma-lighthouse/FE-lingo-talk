package com.lighthouse.domain.constriant

import com.lighthouse.domain.response.ContentVO
import java.lang.reflect.Type

enum class ViewType(
    private val viewTypeClass: Type
) {
    TitleViewType(ContentVO.TitleContent::class.java),
    ChatRoomInfoViewType(ContentVO.ChatRoomContent::class.java),
    UnknownViewType(ContentVO.UnknownContent::class.java);


    companion object {
        fun findClassByItsName(viewTypeString: String?): ViewType {
            values().find { it.name == viewTypeString }?.let {
                return it
            } ?: return UnknownViewType
        }

        fun findViewTypeClassByItsName(viewTypeString: String?): Type {
            return findClassByItsName(viewTypeString).viewTypeClass
        }

        fun getViewTypeByOrdinal(ordinalNum: Int): ViewType {
            return values()[ordinalNum]
        }
    }
}

enum class ColorTest(
    private val color: String
) {
    RED("red"),
    YELLOW("yello");
}

fun test() {
    ColorTest.RED
}