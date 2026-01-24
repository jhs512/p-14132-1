package com.back.boundedContexts.member.config

import com.back.boundedContexts.sharedContexts.member.domain.Member
import com.back.boundedContexts.sharedContexts.member.out.MemberAttrRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class MemberAppConfig(
    memberAttrRepository: MemberAttrRepository,
) {
    init {
        Member.attrRepository_ = memberAttrRepository
    }
}