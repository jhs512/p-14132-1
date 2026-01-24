package com.back.boundedContexts.sharedContexts.member.out

import com.back.boundedContexts.sharedContexts.member.domain.Member
import com.back.boundedContexts.sharedContexts.member.domain.MemberAttr

interface MemberAttrRepositoryCustom {
    fun findBySubjectAndName(subject: Member, name: String): MemberAttr?
}