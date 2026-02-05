package com.back.global.app.config

import com.back.standard.util.Ut
import org.apache.tika.Tika
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import tools.jackson.databind.ObjectMapper

@Configuration
class AppConfig(
    environment: Environment,
    objectMapper: ObjectMapper,
    tika: Tika,
) {
    init {
        Companion.environment = environment
        Ut.JSON.objectMapper = objectMapper
        Companion.tika = tika
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    companion object {
        private lateinit var environment: Environment
        private lateinit var tika: Tika
        private var resourcesSampleDirPath: String? = null

        val isDev: Boolean by lazy { environment.matchesProfiles("dev") }
        val isTest: Boolean by lazy { environment.matchesProfiles("test") }
        val isProd: Boolean by lazy { environment.matchesProfiles("prod") }
        val isNotProd: Boolean by lazy { !isProd }
        val systemMemberApiKey: String by lazy { environment.getProperty("custom.systemMemberApiKey")!! }

        // 사이트 도메인 설정
        val siteCookieDomain: String by lazy { environment.getProperty("custom.site.cookieDomain")!! }
        val siteFrontUrl: String by lazy { environment.getProperty("custom.site.frontUrl")!! }
        val siteBackUrl: String by lazy { environment.getProperty("custom.site.backUrl")!! }

        // 파일 업로드 설정
        val genFileDirPath: String by lazy { environment.getProperty("custom.genFile.dirPath")!! }
        val springServletMultipartMaxFileSize: String by lazy {
            environment.getProperty("spring.servlet.multipart.max-file-size")!!
        }
        val springServletMultipartMaxRequestSize: String by lazy {
            environment.getProperty("spring.servlet.multipart.max-request-size")!!
        }

        @JvmStatic
        fun getTika(): Tika = tika

        @JvmStatic
        fun getTempDirPath(): String = System.getProperty("java.io.tmpdir")

        @JvmStatic
        fun getResourcesSampleDirPath(): String {
            if (resourcesSampleDirPath == null) {
                val resource = ClassPathResource("sample")

                resourcesSampleDirPath = if (resource.exists()) {
                    resource.file.absolutePath
                } else {
                    "src/main/resources/sample"
                }
            }

            return resourcesSampleDirPath!!
        }
    }
}
