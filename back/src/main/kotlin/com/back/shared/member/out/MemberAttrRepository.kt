package com.back.shared.member.out

import com.back.shared.member.domain.MemberAttr
import org.springframework.data.jpa.repository.JpaRepository

interface MemberAttrRepository : JpaRepository<MemberAttr, Int>, MemberAttrRepositoryCustom {
}