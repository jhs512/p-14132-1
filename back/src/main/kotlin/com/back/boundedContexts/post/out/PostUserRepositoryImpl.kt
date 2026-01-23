package com.back.boundedContexts.post.out

import com.back.boundedContexts.post.domain.PostMember
import jakarta.persistence.EntityManager
import org.hibernate.Session

class PostUserRepositoryImpl(
    private val entityManager: EntityManager,
) : PostUserRepositoryCustom {
    override fun findByUsername(username: String): PostMember? {
        return entityManager.unwrap(Session::class.java)
            .byNaturalId(PostMember::class.java)
            .using(PostMember::username.name, username)
            .load()
    }
}
