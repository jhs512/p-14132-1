package com.back.boundedContexts.post.domain

import com.back.global.exceptions.BusinessException
import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne

@Entity
class PostComment(
    @field:ManyToOne(fetch = FetchType.LAZY) val author: PostMember,
    @field:ManyToOne(fetch = FetchType.LAZY) val post: Post,
    var content: String,
) : BaseTime() {
    fun modify(content: String) {
        this.content = content
    }

    fun checkActorCanModify(actor: PostMember) {
        if (author != actor) throw BusinessException("403-1", "${id}번 댓글 수정권한이 없습니다.")
    }

    fun checkActorCanDelete(actor: PostMember) {
        if (author != actor) throw BusinessException("403-2", "${id}번 댓글 삭제권한이 없습니다.")
    }
}