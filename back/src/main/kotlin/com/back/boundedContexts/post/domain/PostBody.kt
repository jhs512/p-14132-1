package com.back.boundedContexts.post.domain

import com.back.global.jpa.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Lob

@Entity
class PostBody(
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    var content: String
) : BaseEntity()