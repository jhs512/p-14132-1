package com.back.boundedContexts.shared.core

import java.util.UUID

/**
 * 모든 Payload의 공통 인터페이스.
 * 고유 식별자(uid)를 가진다.
 */
interface Payload {
    val uid: UUID
}
