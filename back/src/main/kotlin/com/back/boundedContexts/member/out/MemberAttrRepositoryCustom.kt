package com.back.boundedContexts.member.out

import com.back.boundedContexts.member.domain.Member
import com.back.boundedContexts.member.domain.MemberAttr

interface MemberAttrRepositoryCustom {
    fun findBySubjectAndName(subject: Member, name: String): MemberAttr?
}