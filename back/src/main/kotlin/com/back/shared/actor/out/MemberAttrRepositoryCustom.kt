package com.back.shared.actor.out

import com.back.shared.actor.domain.Member
import com.back.shared.actor.domain.MemberAttr

interface MemberAttrRepositoryCustom {
    fun findBySubjectAndName(subject: Member, name: String): MemberAttr?
}