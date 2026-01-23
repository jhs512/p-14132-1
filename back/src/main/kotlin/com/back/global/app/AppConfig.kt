package com.back.global.app

import com.back.boundedContexts.post.domain.PostMember
import com.back.boundedContexts.post.out.PostUserAttrRepository
import com.back.shared.actor.domain.BaseMember
import com.back.shared.actor.domain.Member
import com.back.shared.actor.out.MemberAttrRepository
import com.back.shared.actor.out.MemberRepository
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
    memberAttrRepository: MemberAttrRepository,
    postUserAttrRepository: PostUserAttrRepository,
    memberRepository: MemberRepository,
) {
    init {
        Companion.environment = environment
        Ut.json.objectMapper = objectMapper
        BaseMember.memberRepository = memberRepository
        BaseMember.memberAttrRepository = memberAttrRepository
        Member.attrRepository = memberAttrRepository
        PostMember.attrRepository = postUserAttrRepository
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    companion object {
        private lateinit var environment: Environment

        val isDev: Boolean
            get() = environment.matchesProfiles("dev")

        val isTest: Boolean
            get() = !environment.matchesProfiles("test")

        val isProd: Boolean
            get() = environment.matchesProfiles("prod")

        val isNotProd: Boolean
            get() = !isProd
    }
}