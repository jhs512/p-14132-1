package com.back.boundedContexts.post.domain

import com.back.global.jpa.domain.BaseTime
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.ManyToOne

@Entity
class PostGenFile(
    @field:ManyToOne(fetch = LAZY)
    val post: Post,

    @field:Enumerated(STRING)
    val typeCode: TypeCode,

    val fileNo: Int,
    val originalFileName: String,
    val fileName: String,
    val fileExt: String,
    val fileExtTypeCode: String,
    val fileExtType2Code: String,
    val fileDateDir: String,
    val fileSize: Int,
    val metadata: String = "",
) : BaseTime() {

    enum class TypeCode {
        ATTACHMENT,
        THUMBNAIL,
    }

    val filePath: String
        get() = "$fileDateDir/$fileName"

    val publicUrl: String
        get() = "/gen/$filePath"

    val downloadUrl: String
        get() = "/post/${post.id}/genFile/download/$id/$originalFileName"
}
