package com.back.boundedContexts.post.`in`

import com.back.boundedContexts.post.app.PostFacade
import com.back.boundedContexts.post.app.PostGenFileFacade
import com.back.boundedContexts.post.domain.PostGenFile.TypeCode
import com.back.boundedContexts.post.dto.PostGenFileDto
import com.back.global.dto.RsData
import com.back.global.exception.app.BusinessException
import com.back.global.web.Rq
import com.back.standard.extensions.getOrThrow
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files

@RestController
@RequestMapping("/post/api/v1/posts/{postId}/genFiles")
@Tag(name = "ApiV1PostGenFileController", description = "API 파일 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class ApiV1PostGenFileController(
    private val postFacade: PostFacade,
    private val postGenFileFacade: PostGenFileFacade,
    private val rq: Rq
) {
    val actor
        get() = rq.actor

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "파일 목록 조회")
    fun getItems(
        @PathVariable postId: Int,
        @RequestParam(required = false) typeCode: TypeCode?
    ): List<PostGenFileDto> {
        val post = postFacade.findById(postId).getOrThrow()
        post.checkActorCanRead(rq.actorOrNull)

        return post.genFiles
            .filter { typeCode == null || it.typeCode == typeCode }
            .map { PostGenFileDto(it) }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "파일 단건 조회")
    fun getItem(
        @PathVariable postId: Int,
        @PathVariable id: Int
    ): PostGenFileDto {
        val post = postFacade.findById(postId).getOrThrow()
        post.checkActorCanRead(rq.actorOrNull)

        val genFile = post.genFiles.find { it.id == id }
            ?: throw BusinessException("404-1", "${id}번 파일을 찾을 수 없습니다.")

        return PostGenFileDto(genFile)
    }

    @PostMapping("/{typeCode}")
    @Transactional
    @Operation(summary = "파일 업로드")
    fun upload(
        @PathVariable postId: Int,
        @PathVariable typeCode: TypeCode,
        @RequestParam("file") file: MultipartFile
    ): RsData<PostGenFileDto> {
        val post = postFacade.findById(postId).getOrThrow()
        post.checkActorCanModify(rq.actorOrNull)

        val genFile = postGenFileFacade.upload(post, typeCode, file)

        return RsData(
            "201-1",
            "파일이 업로드되었습니다.",
            PostGenFileDto(genFile)
        )
    }

    @DeleteMapping("/{typeCode}/{fileNo}")
    @Transactional
    @Operation(summary = "파일 삭제")
    fun delete(
        @PathVariable postId: Int,
        @PathVariable typeCode: TypeCode,
        @PathVariable fileNo: Int
    ): RsData<Void> {
        val post = postFacade.findById(postId).getOrThrow()
        post.checkActorCanModify(rq.actorOrNull)

        val genFile = post.findGenFile(typeCode, fileNo)
            ?: throw BusinessException("404-1", "해당 파일을 찾을 수 없습니다.")

        postGenFileFacade.delete(post, genFile)

        return RsData(
            "200-1",
            "파일이 삭제되었습니다."
        )
    }

    @PostMapping("/thumbnail/{fileNo}")
    @Transactional
    @Operation(summary = "썸네일 설정")
    fun setThumbnail(
        @PathVariable postId: Int,
        @PathVariable fileNo: Int
    ): RsData<Void> {
        val post = postFacade.findById(postId).getOrThrow()
        post.checkActorCanModify(rq.actorOrNull)

        val genFile = post.findGenFile(TypeCode.THUMBNAIL, fileNo)
            ?: throw BusinessException("404-1", "해당 썸네일 파일을 찾을 수 없습니다.")

        postGenFileFacade.setThumbnail(post, genFile)

        return RsData(
            "200-1",
            "썸네일이 설정되었습니다."
        )
    }

    @GetMapping("/download/{id}/{fileName}")
    @Transactional(readOnly = true)
    @Operation(summary = "파일 다운로드")
    fun download(
        @PathVariable postId: Int,
        @PathVariable id: Int,
        @PathVariable fileName: String
    ): ResponseEntity<Resource> {
        val post = postFacade.findById(postId).getOrThrow()
        post.checkActorCanRead(rq.actorOrNull)

        val genFile = post.genFiles.find { it.id == id }
            ?: throw BusinessException("404-1", "${id}번 파일을 찾을 수 없습니다.")

        val filePath = postGenFileFacade.getFilePath(genFile)

        if (!Files.exists(filePath)) {
            throw BusinessException("404-2", "파일이 존재하지 않습니다.")
        }

        val resource = InputStreamResource(Files.newInputStream(filePath))
        val encodedFileName = URLEncoder.encode(genFile.originalFileName, StandardCharsets.UTF_8)
            .replace("+", "%20")

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''$encodedFileName")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(genFile.fileSize.toLong())
            .body(resource)
    }
}
