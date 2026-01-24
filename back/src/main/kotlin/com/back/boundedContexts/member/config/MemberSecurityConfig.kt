package com.back.boundedContexts.member.config

import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.AuthorizeHttpRequestsDsl
import org.springframework.stereotype.Component

@Component
class MemberSecurityConfig {
    fun configure(authorize: AuthorizeHttpRequestsDsl) {
        authorize.apply {
            authorize("/api/*/actors/login", permitAll)
            authorize("/api/*/actors/logout", permitAll)
            authorize(HttpMethod.POST, "/api/*/actors", permitAll)
            authorize(HttpMethod.GET, "/api/*/actors/{id:\\d+}/redirectToProfileImg", permitAll)
            authorize("/api/*/members/login", permitAll)
            authorize("/api/*/members/logout", permitAll)
            authorize(HttpMethod.POST, "/api/*/members", permitAll)
            authorize(HttpMethod.GET, "/api/*/members/{id:\\d+}/redirectToProfileImg", permitAll)
        }
    }
}