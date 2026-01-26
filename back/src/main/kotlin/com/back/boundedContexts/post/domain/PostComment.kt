package com.back.boundedContexts.post.domain

import com.back.boundedContexts.shared.exceptions.BusinessException
import com.back.boundedContexts.shared.core.BaseTime
import com.back.boundedContexts.sharedContexts.member.domain.Member
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne

@Entity
class PostComment(
    @field:ManyToOne(fetch = FetchType.LAZY) val author: Member,
    @field:ManyToOne(fetch = FetchType.LAZY) val post: Post,
    var content: String,
) : BaseTime() {
    fun modify(content: String) {
        this.content = content
    }

    fun checkActorCanModify(actor: Member) {
        if (author != actor) throw BusinessException("403-1", "${id}번 댓글 수정권한이 없습니다.")
    }

    fun checkActorCanDelete(actor: Member) {
        if (author != actor) throw BusinessException("403-2", "${id}번 댓글 삭제권한이 없습니다.")
    }
}