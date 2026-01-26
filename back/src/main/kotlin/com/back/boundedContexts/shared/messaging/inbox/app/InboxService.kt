package com.back.boundedContexts.shared.messaging.inbox.app

import com.back.boundedContexts.shared.messaging.inbox.domain.InboxEvent
import com.back.boundedContexts.shared.messaging.inbox.out.InboxEventRepository
import com.back.standard.util.Ut
import org.springframework.stereotype.Service
import java.util.*

@Service
class InboxService(
    private val inboxEventRepository: InboxEventRepository
) {
    fun <T : Any> save(uid: UUID, aggregateType: String, aggregateId: Int, event: T) {
        inboxEventRepository.save(
            InboxEvent(
                uid = uid,
                eventType = event::class.simpleName!!,
                aggregateType = aggregateType,
                aggregateId = aggregateId,
                payload = Ut.JSON.toString(event)
            )
        )
    }

    /**
     * 중복 처리 방지 (멱등성)
     */
    fun isAlreadyProcessed(uid: UUID): Boolean {
        return inboxEventRepository.existsByUid(uid)
    }
}
