package com.back.global.outbox.app

import com.back.global.outbox.domain.OutboxEvent
import com.back.global.outbox.out.OutboxEventRepository
import com.back.standard.dto.EventPayload
import com.back.standard.util.Ut
import org.springframework.stereotype.Service

@Service
class OutboxFacade(
    private val outboxEventRepository: OutboxEventRepository
) {
    fun add(event: EventPayload) {
        outboxEventRepository.save(
            OutboxEvent(
                event.uid,
                event::class.simpleName!!,
                event.aggregateType,
                event.aggregateId,
                payload = Ut.JSON.toString(event)
            )
        )
    }
}
