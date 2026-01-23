package com.back.boundedContexts.post.app

import com.back.boundedContexts.post.domain.PostMember
import com.back.boundedContexts.post.out.PostMemberRepository
import org.springframework.stereotype.Service

@Service
class PostMemberService(
    private val postMemberRepository: PostMemberRepository,
) {
    fun findByUsername(username: String): PostMember? = postMemberRepository.findByUsername(username)
    fun getReferenceById(id: Int): PostMember = postMemberRepository.getReferenceById(id)
}
