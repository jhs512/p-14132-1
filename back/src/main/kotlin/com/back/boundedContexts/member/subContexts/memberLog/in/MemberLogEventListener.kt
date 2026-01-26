package com.back.boundedContexts.member.subContexts.memberLog.`in`

import com.back.boundedContexts.post.event.PostCommentWrittenEvent
import com.back.boundedContexts.shared.task.app.TaskService
import com.back.boundedContexts.shared.task.domain.TaskHandler
import com.back.boundedContexts.sharedContexts.member.domain.AddMemberLogPayload
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MemberLogEventListener(
    private val taskService: TaskService
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handle(event: PostCommentWrittenEvent) {
        taskService.add(AddMemberLogPayload(event))
    }

    @TaskHandler
    fun handle(payload: AddMemberLogPayload) {
        println("handle::AddMemberLogPayload")
    }
}
