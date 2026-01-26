package com.back.boundedContexts.shared.exceptions

import com.back.boundedContexts.shared.rsData.RsData

class BusinessException(private val resultCode: String, private val msg: String) : RuntimeException(
    "$resultCode : $msg"
) {
    val rsData: RsData<Void>
        get() = RsData<Void>(resultCode, msg)
}
