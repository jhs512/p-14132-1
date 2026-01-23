package com.back.boundedContexts.post.`in`

import com.back.boundedContexts.post.app.PostFacade
import com.back.shared.post.dto.PostCommentDto
import com.back.boundedContexts.post.app.PostMemberService
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import com.back.standard.extensions.getOrThrow
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@Tag(name = "ApiV1PostCommentController", description = "API 댓글 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class ApiV1PostCommentController(
    private val postFacade: PostFacade,
    private val rq: Rq,
    private val postMemberService: PostMemberService
) {
    val actor
        get() = rq.postActor

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    fun getItems(
        @PathVariable postId: Int
    ): List<PostCommentDto> {
        val post = postFacade.findById(postId).getOrThrow()

        return post
            .comments
            .map { PostCommentDto(it) }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    fun getItem(
        @PathVariable postId: Int,
        @PathVariable id: Int
    ): PostCommentDto {
        val post = postFacade.findById(postId).getOrThrow()

        val postComment = post.findCommentById(id).getOrThrow()

        return PostCommentDto(postComment)
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    fun delete(
        @PathVariable postId: Int,
        @PathVariable id: Int
    ): RsData<Void> {
        val post = postFacade.findById(postId).getOrThrow()

        val postComment = post.findCommentById(id).getOrThrow()

        postComment.checkActorCanDelete(actor)

        postFacade.deleteComment(post, postComment)

        return RsData(
            "200-1",
            "${id}번 댓글이 삭제되었습니다."
        )
    }

    data class PostCommentModifyReqBody(
        @field:NotBlank
        @field:Size(min = 2, max = 100)
        val content: String
    )

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    fun modify(
        @PathVariable postId: Int,
        @PathVariable id: Int,
        @Valid @RequestBody reqBody: PostCommentModifyReqBody
    ): RsData<Void> {
        val post = postFacade.findById(postId).getOrThrow()

        val postComment = post.findCommentById(id).getOrThrow()

        postComment.checkActorCanModify(actor)

        postFacade.modifyComment(postComment, reqBody.content)

        return RsData(
            "200-1",
            "${id}번 댓글이 수정되었습니다."
        )
    }

    data class PostCommentWriteReqBody(
        @field:NotBlank
        @field:Size(min = 2, max = 100)
        val content: String
    )

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    fun write(
        @PathVariable postId: Int,
        @Valid @RequestBody reqBody: PostCommentWriteReqBody
    ): RsData<PostCommentDto> {
        val post = postFacade.findById(postId).getOrThrow()

        val postComment = postFacade.writeComment(actor, post, reqBody.content)

        return RsData(
            "201-1",
            "${postComment.id}번 댓글이 작성되었습니다.",
            PostCommentDto(postComment)
        )
    }
}