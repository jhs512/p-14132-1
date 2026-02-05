package com.back.boundedContexts.post.dto

data class PostStatsDto(
    val likesCount: Int,
    val commentsCount: Int,
    val actorHasLiked: Boolean,
)
