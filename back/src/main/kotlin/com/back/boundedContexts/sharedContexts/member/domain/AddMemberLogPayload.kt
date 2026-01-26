package com.back.boundedContexts.sharedContexts.member.domain

import com.back.boundedContexts.shared.event.domain.EventPayload
import com.back.boundedContexts.shared.task.domain.TaskPayload

class AddMemberLogPayload(val event: EventPayload) : TaskPayload
