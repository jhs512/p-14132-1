package com.back.global.security

import com.back.shared.actor.app.ActorFacade
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val actorFacade: ActorFacade
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val member = actorFacade.findByUsername(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다.")

        return SecurityUser(
            member.id,
            member.username,
            "",
            member.nickname,
            member.authorities
        )
    }
}