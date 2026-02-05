package com.back.boundedContexts.member.out.shared

import com.back.global.app.config.AppConfig
import com.back.standard.lib.InternalRestClient
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service
class MemberApiClient(
    private val internalRestClient: InternalRestClient
) {
    private val authHeaders = mapOf(
        HttpHeaders.AUTHORIZATION to "Bearer ${AppConfig.systemMemberApiKey}"
    )

    val randomSecureTip: String
        get() {
            val response = internalRestClient.get(
                "/member/api/v1/members/randomSecureTip",
                authHeaders
            )

            return if (response.isOk) response.body else "No secure tip available"
        }
}
