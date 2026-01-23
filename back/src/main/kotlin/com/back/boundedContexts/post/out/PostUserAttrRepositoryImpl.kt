package com.back.boundedContexts.post.out

import com.back.boundedContexts.post.domain.PostMember
import com.back.boundedContexts.post.domain.PostMemberAttr
import jakarta.persistence.EntityManager
import org.hibernate.Session

class PostUserAttrRepositoryImpl(
    private val entityManager: EntityManager,
) : PostUserAttrRepositoryCustom {
    override fun findBySubjectAndName(subject: PostMember, name: String): PostMemberAttr? {
        return entityManager.unwrap(Session::class.java)
            .byNaturalId(PostMemberAttr::class.java)
            .using(PostMemberAttr::subject.name, subject)
            .using(PostMemberAttr::name.name, name)
            .load()
    }
}