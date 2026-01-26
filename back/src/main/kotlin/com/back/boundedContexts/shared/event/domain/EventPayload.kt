package com.back.boundedContexts.shared.event.domain

import com.back.boundedContexts.shared.core.Payload
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * 도메인 이벤트 Payload.
 * Aggregate 정보(type, id)를 포함한다.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
interface EventPayload : Payload {
    val aggregateType: String
    val aggregateId: Int
}
