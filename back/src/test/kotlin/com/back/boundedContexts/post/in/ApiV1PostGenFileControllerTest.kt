package com.back.boundedContexts.post.`in`

import com.back.boundedContexts.member.app.shared.ActorFacade
import com.back.boundedContexts.post.app.PostFacade
import com.back.boundedContexts.post.app.PostGenFileFacade
import com.back.boundedContexts.post.domain.PostGenFile.TypeCode
import com.back.standard.extensions.getOrThrow
import com.back.standard.sampleResource.SampleResource
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.io.File

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiV1PostGenFileControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var postFacade: PostFacade

    @Autowired
    private lateinit var postGenFileFacade: PostGenFileFacade

    @Autowired
    private lateinit var actorFacade: ActorFacade

    @Test
    @DisplayName("파일 업로드")
    @WithUserDetails("user1")
    fun t1() {
        val postId = 1

        val file = MockMultipartFile(
            "files",
            "test-image.png",
            "image/png",
            "test image content".toByteArray()
        )

        val resultActions = mvc
            .perform(
                multipart("/post/api/v1/posts/$postId/genFiles/ATTACHMENT")
                    .file(file)
            )
            .andDo(print())

        resultActions
            .andExpect(handler().handlerType(ApiV1PostGenFileController::class.java))
            .andExpect(handler().methodName("upload"))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.resultCode").value("201-1"))
            .andExpect(jsonPath("$.msg").value("1개의 파일이 업로드되었습니다."))
            .andExpect(jsonPath("$.data[0].postId").value(postId))
            .andExpect(jsonPath("$.data[0].typeCode").value("ATTACHMENT"))
            .andExpect(jsonPath("$.data[0].fileNo").value(1))
            .andExpect(jsonPath("$.data[0].originalFileName").value("test-image.png"))
            .andExpect(jsonPath("$.data[0].fileExt").value("png"))
            .andExpect(jsonPath("$.data[0].fileExtTypeCode").value("img"))
    }

    @Test
    @DisplayName("파일 업로드, 권한 없음")
    @WithUserDetails("user2")
    fun t2() {
        val postId = 1  // user1's post

        val file = MockMultipartFile(
            "files",
            "test-image.png",
            "image/png",
            "test image content".toByteArray()
        )

        val resultActions = mvc
            .perform(
                multipart("/post/api/v1/posts/$postId/genFiles/ATTACHMENT")
                    .file(file)
            )
            .andDo(print())

        resultActions
            .andExpect(handler().handlerType(ApiV1PostGenFileController::class.java))
            .andExpect(handler().methodName("upload"))
            .andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("파일 목록 조회")
    @WithUserDetails("user1")
    fun t3() {
        val postId = 1
        val post = postFacade.findById(postId).getOrThrow()
        val actor = actorFacade.findByUsername("user1").getOrThrow()

        // 파일 업로드 (테스트 데이터)
        val file = MockMultipartFile(
            "file",
            "test.png",
            "image/png",
            "test".toByteArray()
        )
        postGenFileFacade.upload(post, TypeCode.ATTACHMENT, file)

        val resultActions = mvc
            .perform(
                get("/post/api/v1/posts/$postId/genFiles")
            )
            .andDo(print())

        resultActions
            .andExpect(handler().handlerType(ApiV1PostGenFileController::class.java))
            .andExpect(handler().methodName("getItems"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].typeCode").value("ATTACHMENT"))
    }

    @Test
    @DisplayName("파일 목록 조회, typeCode 필터")
    @WithUserDetails("user1")
    fun t4() {
        val postId = 1
        val post = postFacade.findById(postId).getOrThrow()

        // 첨부파일과 썸네일 각각 업로드
        val attachmentFile = MockMultipartFile("file", "attach.png", "image/png", "test".toByteArray())
        val thumbnailFile = MockMultipartFile("file", "thumb.png", "image/png", "test".toByteArray())
        postGenFileFacade.upload(post, TypeCode.ATTACHMENT, attachmentFile)
        postGenFileFacade.upload(post, TypeCode.THUMBNAIL, thumbnailFile)

        val resultActions = mvc
            .perform(
                get("/post/api/v1/posts/$postId/genFiles")
                    .param("typeCode", "THUMBNAIL")
            )
            .andDo(print())

        resultActions
            .andExpect(handler().handlerType(ApiV1PostGenFileController::class.java))
            .andExpect(handler().methodName("getItems"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].typeCode").value("THUMBNAIL"))
    }

    @Test
    @DisplayName("파일 삭제")
    @WithUserDetails("user1")
    fun t5() {
        val postId = 1
        val post = postFacade.findById(postId).getOrThrow()

        // 파일 업로드
        val file = MockMultipartFile("file", "test.png", "image/png", "test".toByteArray())
        postGenFileFacade.upload(post, TypeCode.ATTACHMENT, file)

        val resultActions = mvc
            .perform(
                delete("/post/api/v1/posts/$postId/genFiles/ATTACHMENT/1")
            )
            .andDo(print())

        resultActions
            .andExpect(handler().handlerType(ApiV1PostGenFileController::class.java))
            .andExpect(handler().methodName("delete"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("200-1"))
            .andExpect(jsonPath("$.msg").value("파일이 삭제되었습니다."))
    }

    @Test
    @DisplayName("파일 삭제, 권한 없음")
    @WithUserDetails("user2")
    fun t6() {
        val postId = 1  // user1's post
        val post = postFacade.findById(postId).getOrThrow()

        // 파일 업로드 (작성자인 user1이 아닌 상태에서)
        val file = MockMultipartFile("file", "test.png", "image/png", "test".toByteArray())
        postGenFileFacade.upload(post, TypeCode.ATTACHMENT, file)

        val resultActions = mvc
            .perform(
                delete("/post/api/v1/posts/$postId/genFiles/ATTACHMENT/1")
            )
            .andDo(print())

        resultActions
            .andExpect(handler().handlerType(ApiV1PostGenFileController::class.java))
            .andExpect(handler().methodName("delete"))
            .andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("썸네일 설정")
    @WithUserDetails("user1")
    fun t7() {
        val postId = 1
        val post = postFacade.findById(postId).getOrThrow()

        // 썸네일 파일 업로드
        val file = MockMultipartFile("file", "thumb.png", "image/png", "test".toByteArray())
        postGenFileFacade.upload(post, TypeCode.THUMBNAIL, file)

        val resultActions = mvc
            .perform(
                post("/post/api/v1/posts/$postId/genFiles/thumbnail/1")
            )
            .andDo(print())

        resultActions
            .andExpect(handler().handlerType(ApiV1PostGenFileController::class.java))
            .andExpect(handler().methodName("setThumbnail"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("200-1"))
            .andExpect(jsonPath("$.msg").value("썸네일이 설정되었습니다."))
    }

    @Test
    @DisplayName("파일 단건 조회")
    fun t8() {
        val postId = 1
        val post = postFacade.findById(postId).getOrThrow()

        // 파일 업로드
        val file = MockMultipartFile("file", "test.png", "image/png", "test".toByteArray())
        val genFile = postGenFileFacade.upload(post, TypeCode.ATTACHMENT, file)

        val resultActions = mvc
            .perform(
                get("/post/api/v1/posts/$postId/genFiles/${genFile.id}")
            )
            .andDo(print())

        resultActions
            .andExpect(handler().handlerType(ApiV1PostGenFileController::class.java))
            .andExpect(handler().methodName("getItem"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(genFile.id))
            .andExpect(jsonPath("$.postId").value(postId))
            .andExpect(jsonPath("$.typeCode").value("ATTACHMENT"))
            .andExpect(jsonPath("$.originalFileName").value("test.png"))
    }

    @Test
    @DisplayName("파일 다운로드")
    fun t9() {
        val postId = 1
        val post = postFacade.findById(postId).getOrThrow()

        // 파일 업로드
        val fileContent = "test file content"
        val file = MockMultipartFile("file", "test.txt", "text/plain", fileContent.toByteArray())
        val genFile = postGenFileFacade.upload(post, TypeCode.ATTACHMENT, file)

        val resultActions = mvc
            .perform(
                get("/post/api/v1/posts/$postId/genFiles/download/${genFile.id}/${genFile.originalFileName}")
            )
            .andDo(print())

        resultActions
            .andExpect(handler().handlerType(ApiV1PostGenFileController::class.java))
            .andExpect(handler().methodName("download"))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Disposition", Matchers.containsString("attachment")))
            .andExpect(content().bytes(fileContent.toByteArray()))
    }

    @Test
    @DisplayName("실제 이미지 파일 업로드 (SampleResource 사용)")
    @WithUserDetails("user1")
    fun t10() {
        val postId = 1
        val sample = SampleResource.IMG_JPG_SAMPLE1
        val sampleFile = File(sample.getFilePath())

        val file = MockMultipartFile(
            "files",
            sample.getOriginalFileName(),
            sample.getContentType(),
            sampleFile.readBytes()
        )

        val resultActions = mvc
            .perform(
                multipart("/post/api/v1/posts/$postId/genFiles/ATTACHMENT")
                    .file(file)
            )
            .andDo(print())

        resultActions
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data[0].originalFileName").value(sample.getOriginalFileName()))
            .andExpect(jsonPath("$.data[0].fileExt").value(sample.fileExt))
            .andExpect(jsonPath("$.data[0].fileExtTypeCode").value("img"))
    }

    @Test
    @DisplayName("실제 오디오 파일 업로드 (SampleResource 사용)")
    @WithUserDetails("user1")
    fun t11() {
        val postId = 1
        val sample = SampleResource.AUDIO_MP3_SAMPLE2
        val sampleFile = File(sample.getFilePath())

        val file = MockMultipartFile(
            "files",
            sample.getOriginalFileName(),
            sample.getContentType(),
            sampleFile.readBytes()
        )

        val resultActions = mvc
            .perform(
                multipart("/post/api/v1/posts/$postId/genFiles/ATTACHMENT")
                    .file(file)
            )
            .andDo(print())

        resultActions
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data[0].originalFileName").value(sample.getOriginalFileName()))
            .andExpect(jsonPath("$.data[0].fileExt").value(sample.fileExt))
            .andExpect(jsonPath("$.data[0].fileExtTypeCode").value("audio"))
    }
}
