package com.back.boundedContexts.sharedContexts.outbox.out

import com.back.boundedContexts.sharedContexts.outbox.domain.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository

interface OutboxEventRepository : JpaRepository<OutboxEvent, Int>
