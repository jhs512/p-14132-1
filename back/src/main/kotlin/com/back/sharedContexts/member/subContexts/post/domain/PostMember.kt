package com.back.sharedContexts.member.subContexts.post.domain

interface PostMember {
    val id: Int
    val name: String

    var postsCount: Int
    var postCommentsCount: Int

    fun incrementPostsCount() {
        postsCount++
    }

    fun decrementPostsCount() {
        postsCount--
    }

    fun incrementPostCommentsCount() {
        postCommentsCount++
    }

    fun decrementPostCommentsCount() {
        postCommentsCount--
    }
}
