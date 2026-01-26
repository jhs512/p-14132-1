package com.back.boundedContexts.shared.event.app

import com.back.boundedContexts.shared.event.domain.EventPayload
import com.back.boundedContexts.shared.messaging.inbox.app.InboxService
import com.back.boundedContexts.shared.messaging.outbox.app.OutboxService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

/**
 * 도메인 이벤트 발행 서비스.
 * 직교적 설계: 각 기능을 독립적으로 조합 가능.
 *
 * - publishLocal(): Spring Event만 (동기 처리)
 * - publishWithOutbox(): Outbox 저장 + Spring Event (외부 발행 필요 시)
 * - publishWithInbox(): Inbox 저장 + Spring Event (멱등성 보장 필요 시)
 * - publish(): Outbox + Inbox + Spring Event (전체)
 */
@Service
class EventPublisher(
    private val outboxService: OutboxService,
    private val inboxService: InboxService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    /**
     * Spring Event만 발행 (동기, 로컬 처리만)
     */
    fun publishLocal(event: EventPayload) {
        applicationEventPublisher.publishEvent(event)
    }

    /**
     * Outbox 저장 + Spring Event 발행
     * 외부 시스템으로 발행이 필요할 때 사용
     */
    fun publishWithOutbox(event: EventPayload) {
        outboxService.save(
            uid = event.uid,
            aggregateType = event.aggregateType,
            aggregateId = event.aggregateId,
            event = event
        )
        applicationEventPublisher.publishEvent(event)
    }

    /**
     * Inbox 저장 + Spring Event 발행
     * 멱등성 보장이 필요할 때 사용
     */
    fun publishWithInbox(event: EventPayload) {
        inboxService.save(
            uid = event.uid,
            aggregateType = event.aggregateType,
            aggregateId = event.aggregateId,
            event = event
        )
        applicationEventPublisher.publishEvent(event)
    }

    /**
     * Outbox + Inbox + Spring Event 발행 (전체)
     * 외부 발행 + 멱등성 모두 필요할 때 사용
     */
    fun publish(event: EventPayload) {
        outboxService.save(
            uid = event.uid,
            aggregateType = event.aggregateType,
            aggregateId = event.aggregateId,
            event = event
        )
        inboxService.save(
            uid = event.uid,
            aggregateType = event.aggregateType,
            aggregateId = event.aggregateId,
            event = event
        )
        applicationEventPublisher.publishEvent(event)
    }
}
