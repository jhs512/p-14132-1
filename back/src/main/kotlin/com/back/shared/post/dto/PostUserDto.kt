package com.back.shared.post.dto

import com.back.boundedContexts.post.domain.PostMember
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
    constructor(postMember: PostMember) : this(
        postMember.id,
        postMember.createDate,
        postMember.modifyDate,
        postMember.name,
        postMember.redirectToProfileImgUrlOrDefault,
        postMember.postsCount,
        postMember.postCommentsCount
    )
}