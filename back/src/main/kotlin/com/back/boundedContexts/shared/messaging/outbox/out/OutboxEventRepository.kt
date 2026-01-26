package com.back.boundedContexts.shared.messaging.outbox.out

import com.back.boundedContexts.shared.messaging.outbox.domain.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OutboxEventRepository : JpaRepository<OutboxEvent, Int> {
    fun findByUid(uid: UUID): OutboxEvent?
}
