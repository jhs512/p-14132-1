package com.back.boundedContexts.post.domain

import com.back.boundedContexts.member.domain.shared.Member
import com.back.global.exception.app.BusinessException
import com.back.global.jpa.domain.BaseTime
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.CascadeType.REMOVE
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

@Entity
class Post(
    @field:ManyToOne(fetch = LAZY)
    val author: Member,
    var title: String,
    content: String,
    var published: Boolean = false,
    var listed: Boolean = false,
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
                updateModifiedAt()
            }
        }

    // 상태 확인 속성
    val isTemp: Boolean get() = !published
    val isPrivate: Boolean get() = published && !listed

    fun modify(title: String, content: String, published: Boolean? = null, listed: Boolean? = null) {
        this.title = title
        this.content = content
        published?.let { this.published = it }
        listed?.let { this.listed = it }
    }

    // 읽기 권한 확인: 미공개 글은 작성자나 관리자만 볼 수 있음
    fun canRead(actor: Member?): Boolean {
        if (!published) return actor?.id == author.id || actor?.isAdmin == true
        return true
    }

    fun checkActorCanRead(actor: Member?) {
        if (!canRead(actor)) throw BusinessException("403-3", "${id}번 글 조회권한이 없습니다.")
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