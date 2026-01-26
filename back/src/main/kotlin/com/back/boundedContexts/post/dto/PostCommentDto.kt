package com.back.boundedContexts.post.dto

import com.back.boundedContexts.post.domain.PostComment
import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDateTime

data class PostCommentDto @JsonCreator constructor(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val authorId: Int,
    val authorName: String,
    val authorProfileImgUrl: String,
    val postId: Int,
    val content: String
) {
    constructor(postComment: PostComment) : this(
        postComment.id,
        postComment.createDate,
        postComment.modifyDate,
        postComment.author.id,
        postComment.author.name,
        postComment.author.redirectToProfileImgUrlOrDefault,
        postComment.post.id,
        postComment.content
    )
}