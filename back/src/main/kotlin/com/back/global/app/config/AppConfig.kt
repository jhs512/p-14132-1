package com.back.global.app.config

import com.back.standard.util.Ut
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import tools.jackson.databind.ObjectMapper

@Configuration
class AppConfig(
    environment: Environment,
    objectMapper: ObjectMapper,
) {
    init {
        Companion.environment = environment
        Ut.JSON.objectMapper = objectMapper
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    companion object {
        private lateinit var environment: Environment

        val isDev: Boolean by lazy { environment.matchesProfiles("dev") }
        val isTest: Boolean by lazy { environment.matchesProfiles("test") }
        val isProd: Boolean by lazy { environment.matchesProfiles("prod") }
        val isNotProd: Boolean by lazy { !isProd }
    }
}
