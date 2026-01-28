package com.back.boundedContexts.member.subContexts.memberLog.`in`

import com.back.boundedContexts.member.subContexts.memberLog.dto.MemberAddLogPayload
import com.back.boundedContexts.post.event.PostCommentWrittenEvent
import com.back.global.task.app.TaskFacade
import com.back.global.task.domain.TaskHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MemberLogEventListener(
    private val taskFacade: TaskFacade
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handle(event: PostCommentWrittenEvent) {

        taskFacade.add(MemberAddLogPayload(event.uid, event.aggregateType, event.aggregateId, event))
    }

    @TaskHandler
    fun handle(payload: MemberAddLogPayload) {
        println("handle::AddMemberLogPayload")
    }
}
