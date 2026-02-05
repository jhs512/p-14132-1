package com.back.boundedContexts.post.app

import com.back.boundedContexts.member.domain.shared.Member
import com.back.boundedContexts.member.dto.MemberDto
import com.back.boundedContexts.post.domain.Post
import com.back.boundedContexts.post.domain.PostComment
import com.back.boundedContexts.post.dto.PostCommentDto
import com.back.boundedContexts.post.dto.PostDto
import com.back.boundedContexts.post.event.PostCommentWrittenEvent
import com.back.boundedContexts.post.out.PostLikeRepository
import com.back.boundedContexts.post.out.PostRepository
import com.back.global.event.app.EventPublisher
import com.back.standard.dto.post.type1.PostSearchKeywordType1
import com.back.standard.dto.post.type1.PostSearchSortType1
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class PostFacade(
    private val postRepository: PostRepository,
    private val postLikeRepository: PostLikeRepository,
    private val eventPublisher: EventPublisher,
    private val postNotificationService: PostNotificationService,
) {
    fun count(): Long = postRepository.count()

    fun write(
        author: Member,
        title: String,
        content: String,
        published: Boolean = false,
        listed: Boolean = false,
    ): Post {
        val post = Post(
            author = author,
            title = title,
            content = content,
            published = published,
            listed = listed,
        )

        author.incrementPostsCount()

        return postRepository.save(post)
    }

    fun findById(id: Int): Post? = postRepository.findById(id).getOrNull()

    fun modify(
        post: Post,
        title: String,
        content: String,
        published: Boolean? = null,
        listed: Boolean? = null,
    ) {
        // 공개 시 제목/내용 필수 검증
        val willPublish = published ?: post.published
        if (willPublish) {
            require(title.isNotBlank()) { "공개 글의 제목은 필수입니다." }
            require(title.length >= 2) { "공개 글의 제목은 2자 이상이어야 합니다." }
            require(content.isNotBlank()) { "공개 글의 내용은 필수입니다." }
            require(content.length >= 2) { "공개 글의 내용은 2자 이상이어야 합니다." }
        }

        val wasPublishedAndListed = post.published && post.listed
        post.modify(title, content, published, listed)
        val isNowPublishedAndListed = post.published && post.listed

        // 처음 공개될 때만 알림
        if (!wasPublishedAndListed && isNowPublishedAndListed) {
            postNotificationService.notifyNewPost(post)
        }
    }

    fun findPagedByAuthor(
        author: Member,
        page: Int,
        pageSize: Int,
    ): Page<Post> = postRepository.findByAuthorOrderByIdDesc(
        author,
        PageRequest.of(page - 1, pageSize)
    )

    /**
     * 임시저장 글 조회 또는 생성
     * @return Pair(Post, isNew) - 기존 글이면 false, 새로 생성이면 true
     */
    fun getOrCreateTemp(author: Member): Pair<Post, Boolean> {
        val existingTemp = postRepository.findFirstByAuthorAndPublishedFalseOrderByIdDesc(author)
        if (existingTemp != null) {
            return existingTemp to false
        }

        val newPost = Post(
            author = author,
            title = "",
            content = "",
            published = false,
            listed = false,
        )
        author.incrementPostsCount()
        return postRepository.save(newPost) to true
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

    fun deleteComment(post: Post, postComment: PostComment) =
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

    /**
     * 사용자가 좋아요한 글 ID Set 조회 (IN 쿼리 사용)
     */
    fun findLikedPostIds(liker: Member?, posts: List<Post>): Set<Int> {
        if (liker == null || posts.isEmpty()) return emptySet()

        return postLikeRepository
            .findByLikerAndPostIn(liker, posts)
            .map { it.post.id }
            .toSet()
    }
}
