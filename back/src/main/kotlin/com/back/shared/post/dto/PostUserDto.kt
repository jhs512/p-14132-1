package com.back.shared.post.dto

import com.back.shared.member.domain.Member
import java.time.LocalDateTime

data class PostUserDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val name: String,
    val profileImageUrl: String,
    val postsCount: Int,
    val postCommentsCount: Int,
) {
    constructor(member: Member) : this(
        member.id,
        member.createDate,
        member.modifyDate,
        member.name,
        member.redirectToProfileImgUrlOrDefault,
        member.postsCount,
        member.postCommentsCount
    )
}