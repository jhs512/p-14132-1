package com.back.shared.member.app

import com.back.shared.member.domain.Member
import com.back.shared.member.out.MemberAttrRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class MemberConfig(
    memberAttrRepository: MemberAttrRepository,
) {
    init {
        Member.attrRepository = memberAttrRepository
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
