package com.back.boundedContexts.post.dto

import com.back.boundedContexts.post.domain.Post
import java.time.Instant

data class PostNotificationDto(
    val id: Int,
    val title: String,
    val authorId: Int,
    val authorName: String,
    val authorProfileImgUrl: String,
    val createdAt: Instant,
) {
    constructor(post: Post) : this(
        id = post.id,
        title = post.title,
        authorId = post.author.id,
        authorName = post.author.name,
        authorProfileImgUrl = post.author.redirectToProfileImgUrlOrDefault,
        createdAt = post.createdAt,
    )
}
