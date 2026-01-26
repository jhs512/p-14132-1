package com.back.boundedContexts.shared.messaging.inbox.out

import com.back.boundedContexts.shared.messaging.inbox.domain.InboxEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InboxEventRepository : JpaRepository<InboxEvent, Int> {
    fun findByUid(uid: UUID): InboxEvent?
    fun existsByUid(uid: UUID): Boolean
}
