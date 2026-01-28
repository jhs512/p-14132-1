package com.back.global.inbox.app

import com.back.global.inbox.domain.InboxEvent
import com.back.global.inbox.out.InboxEventRepository
import com.back.standard.dto.EventPayload
import com.back.standard.util.Ut
import org.springframework.stereotype.Service
import java.util.*

@Service
class InboxFacade(
    private val inboxEventRepository: InboxEventRepository
) {
    fun add(event: EventPayload) {
        inboxEventRepository.save(
            InboxEvent(
                uid = event.uid,
                eventType = event::class.simpleName!!,
                aggregateType = event.aggregateType,
                aggregateId = event.aggregateId,
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
