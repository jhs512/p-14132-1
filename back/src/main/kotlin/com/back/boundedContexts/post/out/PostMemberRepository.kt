package com.back.boundedContexts.post.out

import com.back.boundedContexts.post.domain.PostMember
import org.springframework.data.jpa.repository.JpaRepository

interface PostMemberRepository : JpaRepository<PostMember, Int>, PostUserRepositoryCustom {
}