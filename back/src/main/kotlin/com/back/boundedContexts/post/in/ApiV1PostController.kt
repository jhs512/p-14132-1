package com.back.boundedContexts.post.`in`

import PageDto
import com.back.boundedContexts.post.app.PostFacade
import com.back.boundedContexts.post.dto.PostDto
import com.back.boundedContexts.post.dto.PostWithContentDto
import com.back.global.dto.RsData
import com.back.global.web.Rq
import com.back.standard.dto.post.type1.PostSearchKeywordType1
import com.back.standard.dto.post.type1.PostSearchSortType1
import com.back.standard.extensions.getOrThrow
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/post/api/v1/posts")
@Tag(name = "ApiV1PostController", description = "API 글 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class ApiV1PostController(
    private val postFacade: PostFacade,
    private val rq: Rq
) {
    val actor
        get() = rq.actor

    private fun makePostWithContentDto(post: com.back.boundedContexts.post.domain.Post): PostWithContentDto {
        val actor = rq.actorOrNull
        return PostWithContentDto(post).apply {
            actorCanModify = post.getCheckActorCanModifyRs(actor).isSuccess
            actorCanDelete = post.getCheckActorCanDeleteRs(actor).isSuccess
        }
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    fun getItems(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "5") pageSize: Int,
        @RequestParam(defaultValue = "ALL") kwType: PostSearchKeywordType1,
        @RequestParam(defaultValue = "") kw: String,
        @RequestParam(defaultValue = "ID") sort: PostSearchSortType1,
    ): PageDto<PostDto> {
        val page: Int = if (page >= 1) {
            page
        } else {
            1
        }

        val pageSize: Int = if (pageSize in 1..30) {
            pageSize
        } else {
            5
        }

        val postPage = postFacade.findPagedByKw(
            kwType,
            kw,
            sort,
            page,
            pageSize
        )

        return PageDto(
            postPage
                .map { post -> PostDto(post) }
        )
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    fun getItem(@PathVariable id: Int): PostWithContentDto {
        val post = postFacade.findById(id).getOrThrow()

        // 미공개 글은 작성자/관리자만 조회 가능
        post.checkActorCanRead(rq.actorOrNull)

        return makePostWithContentDto(post)
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    fun delete(
        @PathVariable id: Int
    ): RsData<Void> {
        val post = postFacade.findById(id).getOrThrow()

        post.checkActorCanDelete(rq.actorOrNull)

        postFacade.delete(post)

        return RsData(
            "200-1",
            "${id}번 글이 삭제되었습니다."
        )
    }

    data class PostWriteReqBody(
        @field:NotBlank
        @field:Size(min = 2, max = 100)
        val title: String,
        @field:NotBlank
        @field:Size(min = 2)
        val content: String,
        val published: Boolean?,
        val listed: Boolean?,
    )

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    fun write(
        @Valid @RequestBody reqBody: PostWriteReqBody
    ): RsData<PostDto> {
        val post = postFacade.write(
            author = actor,
            title = reqBody.title,
            content = reqBody.content,
            published = reqBody.published ?: false,
            listed = reqBody.listed ?: false,
        )

        return RsData(
            "201-1",
            "${post.id}번 글이 작성되었습니다.",
            PostDto(post)
        )
    }

    data class PostModifyReqBody(
        @field:Size(max = 100)
        val title: String,
        val content: String,
        val published: Boolean? = null,
        val listed: Boolean? = null,
    )

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    fun modify(
        @PathVariable id: Int,
        @Valid @RequestBody reqBody: PostModifyReqBody
    ): RsData<PostDto> {
        val post = postFacade.findById(id).getOrThrow()

        post.checkActorCanModify(rq.actorOrNull)

        postFacade.modify(
            post = post,
            title = reqBody.title,
            content = reqBody.content,
            published = reqBody.published,
            listed = reqBody.listed,
        )

        return RsData(
            "200-1",
            "${post.id}번 글이 수정되었습니다.",
            PostDto(post)
        )
    }

    @GetMapping("/mine")
    @Transactional(readOnly = true)
    @Operation(summary = "내 게시물 목록 조회 (임시저장 포함)")
    fun getMine(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
    ): PageDto<PostDto> {
        val validPage = page.coerceAtLeast(1)
        val validPageSize = pageSize.coerceIn(1, 30)

        val postPage = postFacade.findPagedByAuthor(
            author = actor,
            page = validPage,
            pageSize = validPageSize,
        )

        return PageDto(postPage.map { PostDto(it) })
    }

    @PostMapping("/temp")
    @Transactional
    @Operation(summary = "임시저장 생성/조회", description = "기존 임시저장 글이 있으면 반환, 없으면 새로 생성")
    fun getOrCreateTemp(): RsData<PostWithContentDto> {
        val (post, isNew) = postFacade.getOrCreateTemp(actor)

        return if (isNew) {
            RsData("201-1", "임시저장 글이 생성되었습니다.", makePostWithContentDto(post))
        } else {
            RsData("200-1", "기존 임시저장 글을 반환합니다.", makePostWithContentDto(post))
        }
    }
}