package com.back.boundedContexts.member.app

import com.back.shared.actor.domain.Member
import com.back.shared.actor.out.MemberRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class MemberFacade(
    private val memberRepository: MemberRepository,
) {
    fun count(): Long = memberRepository.count() // 단순 위임

    fun findByUsername(username: String): Member? = memberRepository.findByUsername(username)

    fun findById(id: Int): Member? = memberRepository.findById(id).getOrNull()

    fun findAll(): List<Member> = memberRepository.findAll()

    fun findPaged(page: Int, pageSize: Int) = memberRepository.findAll(
        PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"))
    )
}