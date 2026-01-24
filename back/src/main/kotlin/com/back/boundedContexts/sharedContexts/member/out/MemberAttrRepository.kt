package com.back.boundedContexts.sharedContexts.member.out

import com.back.boundedContexts.sharedContexts.member.domain.MemberAttr
import org.springframework.data.jpa.repository.JpaRepository

interface MemberAttrRepository : JpaRepository<MemberAttr, Int>, MemberAttrRepositoryCustom {
}