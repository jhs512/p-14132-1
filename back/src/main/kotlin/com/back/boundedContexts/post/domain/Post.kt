package com.back.boundedContexts.post.domain

import com.back.global.exceptions.BusinessException
import com.back.global.jpa.entity.BaseTime
import com.back.sharedContexts.member.domain.Member
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.CascadeType.REMOVE
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

@Entity
class Post(
    @field:ManyToOne(fetch = LAZY) val author: Member,
    var title: String,
    content: String
) : BaseTime() {
    @OneToOne(fetch = LAZY, cascade = [PERSIST, REMOVE])
    var body: PostBody = PostBody(content)

    @OneToMany(
        mappedBy = "post",
        cascade = [PERSIST, REMOVE],
        orphanRemoval = true
    )
    val comments: MutableList<PostComment> = mutableListOf()

    var content: String
        get() = body.content
        set(value) {
            if (body.content != value) {
                body.content = value
                updateModifyDate()
            }
        }

    fun modify(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun addComment(author: Member, content: String): PostComment {
        val postComment = PostComment(author, this, content)
        comments.add(postComment)

        author.incrementPostCommentsCount()

        return postComment
    }

    fun findCommentById(id: Int): PostComment? = comments.find { it.id == id }

    fun deleteComment(postComment: PostComment): Boolean {
        postComment.author.decrementPostCommentsCount()

        return comments.remove(postComment)
    }

    fun checkActorCanModify(actor: Member) {
        if (author != actor) throw BusinessException("403-1", "${id}번 글 수정권한이 없습니다.")
    }

    fun checkActorCanDelete(actor: Member) {
        if (author != actor) throw BusinessException("403-2", "${id}번 글 삭제권한이 없습니다.")
    }
}