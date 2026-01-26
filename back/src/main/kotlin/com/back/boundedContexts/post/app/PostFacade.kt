package com.back.boundedContexts.post.app

import com.back.boundedContexts.member.dto.MemberDto
import com.back.boundedContexts.post.domain.Post
import com.back.boundedContexts.post.domain.PostComment
import com.back.boundedContexts.post.dto.PostCommentDto
import com.back.boundedContexts.post.dto.PostDto
import com.back.boundedContexts.post.event.PostCommentWrittenEvent
import com.back.boundedContexts.post.out.PostRepository
import com.back.boundedContexts.shared.event.app.EventPublisher
import com.back.boundedContexts.sharedContexts.member.domain.Member
import com.back.standard.dto.PostSearchKeywordType1
import com.back.standard.dto.PostSearchSortType1
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class PostFacade(
    private val postRepository: PostRepository,
    private val eventPublisher: EventPublisher,
) {
    fun count(): Long = postRepository.count()

    fun write(author: Member, title: String, content: String): Post {
        val post = Post(author, title, content)

        author.incrementPostsCount()

        return postRepository.save(post)
    }

    fun findById(id: Int): Post? = postRepository.findById(id).getOrNull()

    fun modify(post: Post, title: String, content: String) {
        post.modify(title, content)
    }

    fun writeComment(author: Member, post: Post, content: String): PostComment {
        val postComment = post.addComment(author, content)

        postRepository.flush()

        eventPublisher.publish(
            PostCommentWrittenEvent(
                UUID.randomUUID(),
                PostCommentDto(postComment),
                PostDto(post),
                MemberDto(author)
            )
        )

        return postComment
    }

    fun deleteComment(post: Post, postComment: PostComment): Boolean =
        post.deleteComment(postComment)

    fun modifyComment(postComment: PostComment, content: String) {
        postComment.modify(content)
    }

    fun delete(post: Post) {
        post.author.decrementPostsCount()

        postRepository.delete(post)
    }

    fun findLatest(): Post? = postRepository.findFirstByOrderByIdDesc()

    fun findPagedByKw(
        kwType: PostSearchKeywordType1,
        kw: String,
        sort: PostSearchSortType1,
        page: Int,
        pageSize: Int
    ): Page<Post> =
        postRepository.findQPagedByKw(
            kwType,
            kw,
            PageRequest.of(
                page - 1,
                pageSize,
                sort.sortBy
            )
        )
}
