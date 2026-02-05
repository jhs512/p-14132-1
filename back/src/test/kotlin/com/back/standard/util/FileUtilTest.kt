package com.back.standard.util

import com.back.global.app.config.AppConfig
import com.back.standard.sampleResource.SampleResource
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
class FileUtilTest {

    @Test
    @DisplayName("t1: 이미지 메타데이터 추출 - JPG")
    fun t1() {
        // given
        val sampleResource = SampleResource.IMG_JPG_SAMPLE1
        val filePath = sampleResource.getFilePath()

        // when
        val metadata = Ut.file.getMetadata(filePath)

        // then
        assertEquals(sampleResource.width, metadata["width"])
        assertEquals(sampleResource.height, metadata["height"])
    }

    @Test
    @DisplayName("t2: 이미지 메타데이터 추출 - GIF")
    fun t2() {
        // given
        val sampleResource = SampleResource.IMG_GIF_SAMPLE1
        val filePath = sampleResource.getFilePath()

        // when
        val metadata = Ut.file.getMetadata(filePath)

        // then
        assertEquals(sampleResource.width, metadata["width"])
        assertEquals(sampleResource.height, metadata["height"])
    }

    @Test
    @DisplayName("t3: 이미지 메타데이터 추출 - WebP")
    fun t3() {
        // given
        val sampleResource = SampleResource.IMG_WEBP_SAMPLE1
        val filePath = sampleResource.getFilePath()

        // when
        val metadata = Ut.file.getMetadata(filePath)

        // then
        assertEquals(sampleResource.width, metadata["width"])
        assertEquals(sampleResource.height, metadata["height"])
    }

    @Test
    @DisplayName("t4: 비이미지 파일 메타데이터 추출 - 빈 맵 반환")
    fun t4() {
        // given
        val sampleResource = SampleResource.AUDIO_M4A_SAMPLE1
        val filePath = sampleResource.getFilePath()

        // when
        val metadata = Ut.file.getMetadata(filePath)

        // then
        assertTrue(metadata.isEmpty())
    }

    @Test
    @DisplayName("t5: 썸네일 생성 - JPG 이미지 리사이즈")
    fun t5() {
        // given
        val sampleResource = SampleResource.IMG_JPG_SAMPLE1
        val srcFilePath = sampleResource.getFilePath()
        val destFilePath = "${AppConfig.getTempDirPath()}/test_thumbnail_${System.currentTimeMillis()}.jpg"
        val maxWidth = 100
        val maxHeight = 100

        // when
        val result = Ut.file.makeThumbnail(srcFilePath, destFilePath, maxWidth, maxHeight)

        // then
        assertTrue(result)
        assertTrue(File(destFilePath).exists())

        val thumbnailMetadata = Ut.file.getMetadata(destFilePath)
        val thumbnailWidth = thumbnailMetadata["width"] as Int
        val thumbnailHeight = thumbnailMetadata["height"] as Int

        // 비율 유지 확인 (원본: 200x300)
        assertTrue(thumbnailWidth <= maxWidth)
        assertTrue(thumbnailHeight <= maxHeight)
        // 세로가 더 긴 이미지이므로 height가 maxHeight와 같고 width는 비율에 맞게 줄어듦
        assertEquals(maxHeight, thumbnailHeight)
        // 200 * (100/300) ≈ 66~67 (반올림 차이)
        assertTrue(thumbnailWidth in 66..67)

        // cleanup
        File(destFilePath).delete()
    }

    @Test
    @DisplayName("t6: 썸네일 생성 - GIF 이미지 리사이즈")
    fun t6() {
        // given
        val sampleResource = SampleResource.IMG_GIF_SAMPLE1
        val srcFilePath = sampleResource.getFilePath()
        val destFilePath = "${AppConfig.getTempDirPath()}/test_thumbnail_${System.currentTimeMillis()}.gif"
        val maxWidth = 50

        // when
        val result = Ut.file.makeThumbnail(srcFilePath, destFilePath, maxWidth)

        // then
        assertTrue(result)
        assertTrue(File(destFilePath).exists())

        val thumbnailMetadata = Ut.file.getMetadata(destFilePath)
        val thumbnailWidth = thumbnailMetadata["width"] as Int
        val thumbnailHeight = thumbnailMetadata["height"] as Int

        assertTrue(thumbnailWidth <= maxWidth)
        assertTrue(thumbnailHeight <= maxWidth) // maxHeight 기본값은 maxWidth

        // cleanup
        File(destFilePath).delete()
    }

    @Test
    @DisplayName("t7: 썸네일 생성 - WebP 이미지를 JPG로 리사이즈")
    fun t7() {
        // given - WebP 입력, JPG 출력 (ImageIO WebP 쓰기 제한으로)
        val sampleResource = SampleResource.IMG_WEBP_SAMPLE1
        val srcFilePath = sampleResource.getFilePath()
        val destFilePath = "${AppConfig.getTempDirPath()}/test_thumbnail_${System.currentTimeMillis()}.jpg"
        val maxWidth = 200

        // when
        val result = Ut.file.makeThumbnail(srcFilePath, destFilePath, maxWidth)

        // then
        assertTrue(result)
        assertTrue(File(destFilePath).exists())

        val thumbnailMetadata = Ut.file.getMetadata(destFilePath)
        val thumbnailWidth = thumbnailMetadata["width"] as Int
        val thumbnailHeight = thumbnailMetadata["height"] as Int

        assertTrue(thumbnailWidth <= maxWidth)
        assertTrue(thumbnailHeight <= maxWidth)

        // cleanup
        File(destFilePath).delete()
    }

    @Test
    @DisplayName("t8: 원본이 충분히 작으면 그대로 복사")
    fun t8() {
        // given
        val sampleResource = SampleResource.IMG_JPG_SAMPLE1 // 200x300
        val srcFilePath = sampleResource.getFilePath()
        val destFilePath = "${AppConfig.getTempDirPath()}/test_thumbnail_${System.currentTimeMillis()}.jpg"
        val maxWidth = 500 // 원본보다 큼

        // when
        val result = Ut.file.makeThumbnail(srcFilePath, destFilePath, maxWidth)

        // then
        assertTrue(result)
        assertTrue(File(destFilePath).exists())

        val thumbnailMetadata = Ut.file.getMetadata(destFilePath)
        // 원본 크기 그대로
        assertEquals(sampleResource.width, thumbnailMetadata["width"])
        assertEquals(sampleResource.height, thumbnailMetadata["height"])

        // cleanup
        File(destFilePath).delete()
    }

    @Test
    @DisplayName("t9: Tika로 MIME 타입 감지")
    fun t9() {
        // given
        val jpgPath = SampleResource.IMG_JPG_SAMPLE1.getFilePath()
        val gifPath = SampleResource.IMG_GIF_SAMPLE1.getFilePath()
        val webpPath = SampleResource.IMG_WEBP_SAMPLE1.getFilePath()
        val m4aPath = SampleResource.AUDIO_M4A_SAMPLE1.getFilePath()

        // when & then
        assertEquals("jpg", Ut.file.getExtensionByTika(jpgPath))
        assertEquals("gif", Ut.file.getExtensionByTika(gifPath))
        assertEquals("webp", Ut.file.getExtensionByTika(webpPath))
        assertEquals("m4a", Ut.file.getExtensionByTika(m4aPath))
    }

    @Test
    @DisplayName("t10: Tika로 정확한 MIME 타입 반환")
    fun t10() {
        // given
        val jpgPath = SampleResource.IMG_JPG_SAMPLE1.getFilePath()

        // when
        val mimeType = Ut.file.getMimeTypeByTika(jpgPath)

        // then
        assertEquals("image/jpeg", mimeType)
    }
}
