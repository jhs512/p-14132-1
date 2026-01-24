package com.back.sharedContexts.member.out

import com.back.sharedContexts.member.domain.MemberAttr
import org.springframework.data.jpa.repository.JpaRepository

interface MemberAttrRepository : JpaRepository<MemberAttr, Int>, MemberAttrRepositoryCustom {
}