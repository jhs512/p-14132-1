package com.back.boundedContexts.post.app

import com.back.boundedContexts.post.domain.Post
import com.back.boundedContexts.post.dto.PostNotificationDto
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class PostNotificationService(
    private val messagingTemplate: SimpMessagingTemplate,
) {
    fun notifyNewPost(post: Post) {
        if (!post.published || !post.listed) return
        if (post.content.length < 100) return

        messagingTemplate.convertAndSend(
            "/topic/posts/new",
            PostNotificationDto(post)
        )
    }
}
