package com.back.boundedContexts.shared.messaging.inbox.domain

import com.back.boundedContexts.shared.core.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.util.*

enum class InboxStatus {
    PENDING, PROCESSED, FAILED
}

@Entity
class InboxEvent(
    @field:Column(unique = true)
    val uid: UUID,
    val eventType: String,
    val aggregateType: String,
    val aggregateId: Int,
    @Column(columnDefinition = "TEXT")
    val payload: String,
    @Enumerated(EnumType.STRING)
    var status: InboxStatus = InboxStatus.PENDING
) : BaseTime()
