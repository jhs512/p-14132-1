package com.back.boundedContexts.post.app

import com.back.boundedContexts.post.domain.PostMember
import com.back.boundedContexts.post.domain.PostMemberAttr
import com.back.boundedContexts.post.out.PostUserAttrRepository
import org.springframework.stereotype.Service

@Service
class PostMemberAttrService(
    private val postUserAttrRepository: PostUserAttrRepository,
) {
    fun findBySubjectAndName(subject: PostMember, name: String) =
        postUserAttrRepository.findBySubjectAndName(subject, name)

    fun incrementPostsCount(subject: PostMember) {
        val attr = findBySubjectAndName(subject, "postsCount")

        if (attr == null) {
            postUserAttrRepository.save(PostMemberAttr(subject, "postsCount", "1"))
        } else {
            val currentCount = attr.value.toIntOrNull() ?: 0
            attr.value = (currentCount + 1).toString()
            postUserAttrRepository.save(attr)
        }
    }

    fun decrementPostsCount(subject: PostMember) {
        val attr = findBySubjectAndName(subject, "postsCount")

        if (attr == null) {
            postUserAttrRepository.save(PostMemberAttr(subject, "postsCount", "-1"))
        } else {
            val currentCount = attr.value.toIntOrNull() ?: 0
            attr.value = (currentCount - 1).toString()
            postUserAttrRepository.save(attr)
        }
    }

    fun incrementPostCommentsCount(subject: PostMember) {
        val attr = findBySubjectAndName(subject, "postCommentsCount")

        if (attr == null) {
            postUserAttrRepository.save(PostMemberAttr(subject, "postCommentsCount", "1"))
        } else {
            val currentCount = attr.value.toIntOrNull() ?: 0
            attr.value = (currentCount + 1).toString()
            postUserAttrRepository.save(attr)
        }
    }

    fun decrementPostCommentsCount(subject: PostMember) {
        val attr = findBySubjectAndName(subject, "postCommentsCount")

        if (attr == null) {
            postUserAttrRepository.save(PostMemberAttr(subject, "postCommentsCount", "-1"))
        } else {
            val currentCount = attr.value.toIntOrNull() ?: 0
            attr.value = (currentCount - 1).toString()
            postUserAttrRepository.save(attr)
        }
    }

    fun getPostsCount(subject: PostMember): Int {
        val attr = findBySubjectAndName(subject, "postsCount")
        val count = attr?.value?.toIntOrNull() ?: 0

        return count
    }

    fun getPostCommentsCount(subject: PostMember): Int {
        val attr = findBySubjectAndName(subject, "postCommentsCount")
        val count = attr?.value?.toIntOrNull() ?: 0

        return count
    }
}
