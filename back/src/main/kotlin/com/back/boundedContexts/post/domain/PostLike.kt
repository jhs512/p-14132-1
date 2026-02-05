package com.back.boundedContexts.post.domain

import com.back.boundedContexts.member.domain.shared.Member
import com.back.global.jpa.domain.BaseTime
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["liker_id", "post_id"])
    ]
)
class PostLike(
    @field:ManyToOne(fetch = FetchType.LAZY)
    val liker: Member,
    @field:ManyToOne(fetch = FetchType.LAZY)
    val post: Post,
) : BaseTime()
