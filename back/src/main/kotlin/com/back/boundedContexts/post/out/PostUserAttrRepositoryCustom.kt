package com.back.boundedContexts.post.out

import com.back.boundedContexts.post.domain.PostMember
import com.back.boundedContexts.post.domain.PostMemberAttr

interface PostUserAttrRepositoryCustom {
    fun findBySubjectAndName(subject: PostMember, name: String): PostMemberAttr?
}