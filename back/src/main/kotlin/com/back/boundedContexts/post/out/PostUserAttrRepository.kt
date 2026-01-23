package com.back.boundedContexts.post.out

import com.back.boundedContexts.post.domain.PostMemberAttr
import org.springframework.data.jpa.repository.JpaRepository

interface PostUserAttrRepository : JpaRepository<PostMemberAttr, Int>, PostUserAttrRepositoryCustom {
}