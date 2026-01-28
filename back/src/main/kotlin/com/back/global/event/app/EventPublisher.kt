package com.back.global.event.app

import com.back.global.inbox.app.InboxFacade
import com.back.global.outbox.app.OutboxFacade
import com.back.standard.dto.EventPayload
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service


/**
 * 도메인 이벤트 발행 서비스.
 **/
@Service
class EventPublisher(
    private val outboxService: OutboxFacade,
    private val inboxService: InboxFacade,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun publish(event: EventPayload) {
        outboxService.add(event)
        inboxService.add(event)

        applicationEventPublisher.publishEvent(event)
    }
}
