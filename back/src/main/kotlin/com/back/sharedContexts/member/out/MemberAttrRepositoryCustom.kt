package com.back.sharedContexts.member.out

import com.back.sharedContexts.member.domain.Member
import com.back.sharedContexts.member.domain.MemberAttr

interface MemberAttrRepositoryCustom {
    fun findBySubjectAndName(subject: Member, name: String): MemberAttr?
}