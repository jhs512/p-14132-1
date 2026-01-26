package com.back.boundedContexts.post.domain

import com.back.boundedContexts.shared.core.BaseEntity
import jakarta.persistence.Entity

@Entity
class PostBody(
    var content: String
) : BaseEntity() {

}