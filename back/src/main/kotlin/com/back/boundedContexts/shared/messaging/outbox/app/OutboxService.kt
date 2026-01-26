package com.back.boundedContexts.shared.messaging.outbox.app

import com.back.boundedContexts.shared.messaging.outbox.domain.OutboxEvent
import com.back.boundedContexts.shared.messaging.outbox.out.OutboxEventRepository
import com.back.standard.util.Ut
import org.springframework.stereotype.Service
import java.util.*

@Service
class OutboxService(
    private val outboxEventRepository: OutboxEventRepository
) {
    fun <T : Any> save(uid: UUID, aggregateType: String, aggregateId: Int, event: T) {
        outboxEventRepository.save(
            OutboxEvent(
                uid = uid,
                eventType = event::class.simpleName!!,
                aggregateType = aggregateType,
                aggregateId = aggregateId,
                payload = Ut.JSON.toString(event)
            )
        )
    }
}
