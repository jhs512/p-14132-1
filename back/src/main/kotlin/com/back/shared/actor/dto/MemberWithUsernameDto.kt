package com.back.shared.actor.dto

import com.back.shared.actor.domain.Member
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class MemberWithUsernameDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    @get:JsonProperty("isAdmin")
    val isAdmin: Boolean,
    val username: String,
    val name: String,
    val profileImageUrl: String,
) {
    constructor(member: Member) : this(
        member.id,
        member.createDate,
        member.modifyDate,
        member.isAdmin,
        member.username,
        member.name,
        member.redirectToProfileImgUrlOrDefault
    )
}
