package com.back.boundedContexts.post.domain

import com.back.boundedContexts.member.domain.shared.Member
import com.back.boundedContexts.post.out.PostAttrRepository
import com.back.boundedContexts.post.out.PostCommentRepository
import com.back.boundedContexts.post.out.PostLikeRepository
import com.back.global.dto.RsData
import com.back.global.exception.app.BusinessException
import com.back.global.jpa.domain.BaseTime
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.CascadeType.REMOVE
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

@Entity
class Post(
    @field:ManyToOne(fetch = LAZY)
    val author: Member,
    var title: String,
    content: String,
    var published: Boolean = false,
    var listed: Boolean = false,
) : BaseTime() {
    @OneToOne(fetch = LAZY, cascade = [PERSIST, REMOVE])
    var body: PostBody = PostBody(content)

    @OneToMany(
        mappedBy = "post",
        cascade = [PERSIST, REMOVE],
        orphanRemoval = true
    )
    val genFiles: MutableList<PostGenFile> = mutableListOf()

    @field:OneToOne(fetch = LAZY, cascade = [PERSIST, REMOVE])
    var thumbnailGenFile: PostGenFile? = null

    @field:OneToOne(fetch = LAZY, cascade = [PERSIST, REMOVE])
    var likesCountAttr: PostAttr? = null

    @field:OneToOne(fetch = LAZY, cascade = [PERSIST, REMOVE])
    var commentsCountAttr: PostAttr? = null

    @field:OneToOne(fetch = LAZY, cascade = [PERSIST, REMOVE])
    var hitCountAttr: PostAttr? = null

    // ================================
    // Companion Object
    // ================================

    companion object {
        lateinit var postLikeRepository_: PostLikeRepository
        val postLikeRepository by lazy { postLikeRepository_ }

        lateinit var postAttrRepository_: PostAttrRepository
        val postAttrRepository by lazy { postAttrRepository_ }

        lateinit var postCommentRepository_: PostCommentRepository
        val postCommentRepository by lazy { postCommentRepository_ }

        // Attr 이름 상수
        private const val LIKES_COUNT = "likesCount"
        private const val COMMENTS_COUNT = "commentsCount"
        private const val HIT_COUNT = "hitCount"
    }

    var content: String
        get() = body.content
        set(value) {
            if (body.content != value) {
                body.content = value
                updateModifiedAt()
            }
        }

    fun modify(title: String, content: String, published: Boolean? = null, listed: Boolean? = null) {
        this.title = title
        this.content = content
        published?.let { this.published = it }
        listed?.let { this.listed = it }
    }

    // 읽기 권한 확인: 미공개 글은 작성자나 관리자만 볼 수 있음
    fun canRead(actor: Member?): Boolean {
        if (!published) return actor?.id == author.id || actor?.isAdmin == true
        return true
    }

    fun checkActorCanRead(actor: Member?) {
        if (!canRead(actor)) throw BusinessException("403-3", "${id}번 글 조회권한이 없습니다.")
    }

    // ================================
    // 댓글 관리 (PostAttr + Repository 기반)
    // ================================

    val commentsCount: Int
        get() = commentsCountAttr?.value?.toIntOrNull() ?: 0

    private fun setCommentsCount(value: Int) {
        if (commentsCountAttr == null)
            commentsCountAttr = PostAttr(this, COMMENTS_COUNT, value.toString())
        else
            commentsCountAttr!!.value = value.toString()

        postAttrRepository.save(commentsCountAttr!!)
    }

    fun getComments(): List<PostComment> = postCommentRepository.findByPostOrderByIdDesc(this)

    fun findCommentById(id: Int): PostComment? = postCommentRepository.findByPostAndId(this, id)

    fun addComment(author: Member, content: String): PostComment {
        val postComment = PostComment(author, this, content)
        postCommentRepository.save(postComment)

        setCommentsCount(commentsCount + 1)
        author.incrementPostCommentsCount()

        return postComment
    }

    fun deleteComment(postComment: PostComment) {
        postComment.author.decrementPostCommentsCount()
        setCommentsCount(commentsCount - 1)

        postCommentRepository.delete(postComment)
    }

    // 수정 권한 체크 (RsData 반환)
    fun getCheckActorCanModifyRs(actor: Member?): RsData<Void> {
        if (actor == null) return RsData.fail("401-1", "로그인 후 이용해주세요.")
        if (actor == author) return RsData.OK
        return RsData.fail("403-1", "작성자만 글을 수정할 수 있습니다.")
    }

    fun checkActorCanModify(actor: Member?) {
        val rs = getCheckActorCanModifyRs(actor)
        if (rs.isFail) throw BusinessException(rs.resultCode, rs.msg)
    }

    // 삭제 권한 체크 (RsData 반환) - 관리자도 삭제 가능
    fun getCheckActorCanDeleteRs(actor: Member?): RsData<Void> {
        if (actor == null) return RsData.fail("401-1", "로그인 후 이용해주세요.")
        if (actor.isAdmin) return RsData.OK
        if (actor == author) return RsData.OK
        return RsData.fail("403-2", "작성자만 글을 삭제할 수 있습니다.")
    }

    fun checkActorCanDelete(actor: Member?) {
        val rs = getCheckActorCanDeleteRs(actor)
        if (rs.isFail) throw BusinessException(rs.resultCode, rs.msg)
    }

    // 파일 관리
    fun addGenFile(genFile: PostGenFile) {
        genFiles.add(genFile)
    }

    fun findGenFile(typeCode: PostGenFile.TypeCode, fileNo: Int): PostGenFile? =
        genFiles.find { it.typeCode == typeCode && it.fileNo == fileNo }

    fun findGenFileById(id: Int): PostGenFile? =
        genFiles.find { it.id == id }

    fun deleteGenFile(genFile: PostGenFile): Boolean {
        if (thumbnailGenFile?.id == genFile.id) {
            thumbnailGenFile = null
        }
        return genFiles.remove(genFile)
    }

    fun getNextFileNo(typeCode: PostGenFile.TypeCode): Int =
        genFiles.filter { it.typeCode == typeCode }.maxOfOrNull { it.fileNo }?.plus(1) ?: 1

    // ================================
    // 좋아요 관리 (PostAttr 기반)
    // ================================

    val likesCount: Int
        get() = likesCountAttr?.value?.toIntOrNull() ?: 0

    private fun setLikesCount(value: Int) {
        if (likesCountAttr == null)
            likesCountAttr = PostAttr(this, LIKES_COUNT, value.toString())
        else
            likesCountAttr!!.value = value.toString()

        postAttrRepository.save(likesCountAttr!!)
    }

    fun isLikedBy(liker: Member?): Boolean {
        if (liker == null) return false
        return postLikeRepository.findByLikerAndPost(liker, this) != null
    }

    fun toggleLike(liker: Member): Boolean {
        val existingLike = postLikeRepository.findByLikerAndPost(liker, this)

        return if (existingLike != null) {
            postLikeRepository.delete(existingLike)
            setLikesCount(likesCount - 1)
            false // 좋아요 취소됨
        } else {
            val newLike = PostLike(liker, this)
            postLikeRepository.save(newLike)
            setLikesCount(likesCount + 1)
            true // 좋아요 추가됨
        }
    }

    // ================================
    // 조회수 관리 (PostAttr 기반)
    // ================================

    val hitCount: Int
        get() = hitCountAttr?.value?.toIntOrNull() ?: 0

    fun incrementHitCount() {
        if (hitCountAttr == null) {
            hitCountAttr = PostAttr(this, HIT_COUNT, "1")
        } else {
            hitCountAttr!!.value = (hitCount + 1).toString()
        }
        postAttrRepository.save(hitCountAttr!!)
    }
}