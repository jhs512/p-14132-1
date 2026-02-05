package com.back.boundedContexts.post.app

import com.back.boundedContexts.post.domain.Post
import com.back.boundedContexts.post.domain.PostGenFile
import com.back.boundedContexts.post.domain.PostGenFile.TypeCode
import com.back.boundedContexts.post.out.PostGenFileRepository
import com.back.boundedContexts.post.out.PostRepository
import com.back.global.exception.app.BusinessException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class PostGenFileFacade(
    private val postGenFileRepository: PostGenFileRepository,
    private val postRepository: PostRepository,
    @Value("\${custom.genFile.dirPath:./gen_files}")
    private val genFileDirPath: String,
) {
    private val dateDirFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd")
        .withZone(ZoneId.systemDefault())

    fun upload(post: Post, typeCode: TypeCode, file: MultipartFile): PostGenFile {
        val originalFileName = file.originalFilename
            ?: throw BusinessException("400-1", "파일명이 없습니다.")

        val fileExt = originalFileName.substringAfterLast('.', "").lowercase()
        val (fileExtTypeCode, fileExtType2Code) = detectFileType(fileExt)
        val fileName = "${UUID.randomUUID()}.$fileExt"
        val fileDateDir = dateDirFormatter.format(Instant.now())
        val fileNo = post.getNextFileNo(typeCode)

        // 파일 저장
        val targetDir = Path.of(genFileDirPath, fileDateDir)
        Files.createDirectories(targetDir)
        val targetPath = targetDir.resolve(fileName)
        Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)

        val genFile = PostGenFile(
            post = post,
            typeCode = typeCode,
            fileNo = fileNo,
            originalFileName = originalFileName,
            fileName = fileName,
            fileExt = fileExt,
            fileExtTypeCode = fileExtTypeCode,
            fileExtType2Code = fileExtType2Code,
            fileDateDir = fileDateDir,
            fileSize = file.size.toInt(),
        )

        post.addGenFile(genFile)
        postRepository.flush()

        return genFile
    }

    fun delete(post: Post, genFile: PostGenFile) {
        // 파일 시스템에서 삭제
        val filePath = Path.of(genFileDirPath, genFile.fileDateDir, genFile.fileName)
        Files.deleteIfExists(filePath)

        // 엔티티에서 제거
        post.deleteGenFile(genFile)
    }

    fun setThumbnail(post: Post, genFile: PostGenFile) {
        if (genFile.typeCode != TypeCode.THUMBNAIL) {
            throw BusinessException("400-2", "썸네일 타입의 파일만 썸네일로 설정할 수 있습니다.")
        }
        post.thumbnailGenFile = genFile
    }

    fun getFilePath(genFile: PostGenFile): Path =
        Path.of(genFileDirPath, genFile.fileDateDir, genFile.fileName)

    private fun detectFileType(ext: String): Pair<String, String> {
        return when (ext.lowercase()) {
            "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg" -> "img" to ext
            "mp4", "webm", "avi", "mov", "mkv" -> "video" to ext
            "mp3", "wav", "ogg", "flac" -> "audio" to ext
            "pdf" -> "doc" to ext
            "doc", "docx" -> "doc" to "word"
            "xls", "xlsx" -> "doc" to "excel"
            "ppt", "pptx" -> "doc" to "powerpoint"
            "zip", "tar", "gz", "7z", "rar" -> "archive" to ext
            else -> "etc" to ext
        }
    }
}
