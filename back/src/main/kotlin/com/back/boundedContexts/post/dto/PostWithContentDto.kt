package com.back.boundedContexts.post.dto

import com.back.boundedContexts.post.domain.Post
import com.fasterxml.jackson.annotation.JsonCreator
import com.querydsl.core.types.Projections.constructor
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import java.time.Instant

data class PostWithContentDto @JsonCreator constructor(
    val id: Int,
    val createdAt: Instant,
    val modifiedAt: Instant,
    val authorId: Int,
    val authorName: String,
    val authorProfileImgUrl: String,
    val title: String,
    val content: String
) {
    constructor(post: Post) : this(
        id = post.id,
        createdAt = post.createdAt,
        modifiedAt = post.modifiedAt,
        authorId = post.author.id,
        authorName = post.author.name,
        authorProfileImgUrl = post.author.redirectToProfileImgUrlOrDefault,
        title = post.title,
        content = post.content
    )
}