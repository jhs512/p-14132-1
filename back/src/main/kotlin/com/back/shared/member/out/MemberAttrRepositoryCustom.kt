package com.back.shared.member.out

import com.back.shared.member.domain.Member
import com.back.shared.member.domain.MemberAttr

interface MemberAttrRepositoryCustom {
    fun findBySubjectAndName(subject: Member, name: String): MemberAttr?
}