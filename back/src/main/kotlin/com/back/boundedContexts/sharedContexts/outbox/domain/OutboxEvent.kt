package com.back.boundedContexts.sharedContexts.outbox.domain

import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class OutboxEvent(
    val eventType: String,

    val aggregateType: String,

    val aggregateId: Int,

    @Column(columnDefinition = "TEXT")
    val payload: String
) : BaseTime()
