package com.back.boundedContexts.member.app.shared

import com.back.boundedContexts.member.app.shared.AuthTokenService
import com.back.boundedContexts.member.domain.shared.Member
import com.back.boundedContexts.member.out.shared.MemberRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class ActorFacade(
    private val authTokenService: AuthTokenService,
    private val memberRepository: MemberRepository,
) {
    fun findByUsername(username: String): Member? = memberRepository.findByUsername(username)

    fun findByApiKey(apiKey: String): Member? = memberRepository.findByApiKey(apiKey)

    fun genAccessToken(member: Member): String = authTokenService.genAccessToken(member)

    fun payload(accessToken: String) = authTokenService.payload(accessToken)

    fun findById(id: Int): Member? = memberRepository.findById(id).getOrNull()

    fun getReferenceById(id: Int): Member = memberRepository.getReferenceById(id)
}