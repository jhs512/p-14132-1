package com.back.boundedContexts.post.dto

import com.back.boundedContexts.post.domain.PostGenFile
import com.fasterxml.jackson.annotation.JsonCreator
import java.time.Instant

data class PostGenFileDto @JsonCreator constructor(
    val id: Int,
    val createdAt: Instant,
    val modifiedAt: Instant,
    val postId: Int,
    val typeCode: PostGenFile.TypeCode,
    val fileNo: Int,
    val originalFileName: String,
    val fileName: String,
    val fileExt: String,
    val fileExtTypeCode: String,
    val fileExtType2Code: String,
    val fileDateDir: String,
    val fileSize: Int,
    val metadata: String,
    val filePath: String,
    val publicUrl: String,
    val downloadUrl: String,
) {
    constructor(genFile: PostGenFile) : this(
        id = genFile.id,
        createdAt = genFile.createdAt,
        modifiedAt = genFile.modifiedAt,
        postId = genFile.post.id,
        typeCode = genFile.typeCode,
        fileNo = genFile.fileNo,
        originalFileName = genFile.originalFileName,
        fileName = genFile.fileName,
        fileExt = genFile.fileExt,
        fileExtTypeCode = genFile.fileExtTypeCode,
        fileExtType2Code = genFile.fileExtType2Code,
        fileDateDir = genFile.fileDateDir,
        fileSize = genFile.fileSize,
        metadata = genFile.metadata,
        filePath = genFile.filePath,
        publicUrl = genFile.publicUrl,
        downloadUrl = genFile.downloadUrl,
    )
}
