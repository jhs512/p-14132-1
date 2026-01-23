package com.back.boundedContexts.member.out

import com.back.boundedContexts.member.domain.Member
import com.back.boundedContexts.member.domain.MemberAttr
import jakarta.persistence.EntityManager
import org.hibernate.Session

class MemberAttrRepositoryImpl(
    private val entityManager: EntityManager,
) : MemberAttrRepositoryCustom {
    override fun findBySubjectAndName(subject: Member, name: String): MemberAttr? {
        return entityManager.unwrap(Session::class.java)
            .byNaturalId(MemberAttr::class.java)
            .using(MemberAttr::subject.name, subject)
            .using(MemberAttr::name.name, name)
            .load()
    }
}