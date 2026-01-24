package com.back.boundedContexts.member.subContexts.memberLog.`in`

import com.back.boundedContexts.member.subContexts.memberLog.app.MemberLogFacade
import com.back.sharedContexts.post.event.PostCommentWrittenEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MemberLogEventListener(
    private val memberLogFacade: MemberLogFacade,
) {
    @TransactionalEventListener
    fun handle(event: PostCommentWrittenEvent) {
        memberLogFacade.save(event)
    }
}