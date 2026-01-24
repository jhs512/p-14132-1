package com.back.sharedContexts.post.event

import com.back.sharedContexts.post.dto.PostCommentDto
import com.back.sharedContexts.post.dto.PostDto
import com.back.sharedContexts.post.dto.PostUserDto
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class PostCommentWrittenEvent(
    @field:JsonIgnoreProperties("content")
    val postCommentDto: PostCommentDto,
    @field:JsonIgnoreProperties("title", "content")
    val postDto: PostDto,
    @field:JsonIgnore
    val actorDto: PostUserDto
) {

}