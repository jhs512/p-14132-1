package com.back.boundedContexts.member.config

import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.AuthorizeHttpRequestsDsl
import org.springframework.stereotype.Component

@Component
class MemberSecurityConfig {
    fun configure(authorize: AuthorizeHttpRequestsDsl) {
        authorize.apply {
            authorize("/api/*/members/login", permitAll)
            authorize("/api/*/members/logout", permitAll)
            authorize(HttpMethod.POST, "/api/*/members", permitAll)
            authorize(HttpMethod.GET, "/api/*/members/{id:\\d+}/redirectToProfileImg", permitAll)
        }
    }
}