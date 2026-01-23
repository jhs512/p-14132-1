package com.back.boundedContexts.post.out

import com.back.boundedContexts.post.domain.PostMember

interface PostUserRepositoryCustom {
    fun findByUsername(username: String): PostMember?
}
