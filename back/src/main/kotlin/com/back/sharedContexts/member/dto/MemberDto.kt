package com.back.sharedContexts.member.dto

import com.back.sharedContexts.member.domain.Member
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class MemberDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    @get:JsonProperty("isAdmin")
    val isAdmin: Boolean,
    val name: String,
    val profileImageUrl: String,
) {
    constructor(member: Member) : this(
        member.id,
        member.createDate,
        member.modifyDate,
        member.isAdmin,
        member.name,
        member.redirectToProfileImgUrlOrDefault
    )
}
