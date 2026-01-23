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

    private fun updateCount(subject: PostMember, attrName: String, delta: Int) {
        val attr = findBySubjectAndName(subject, attrName)
        val newValue = ((attr?.value?.toIntOrNull() ?: 0) + delta).toString()

        postUserAttrRepository.save(
            attr?.apply { value = newValue } ?: PostMemberAttr(subject, attrName, newValue)
        )
    }

    private fun getCount(subject: PostMember, attrName: String): Int =
        findBySubjectAndName(subject, attrName)?.value?.toIntOrNull() ?: 0

    fun incrementPostsCount(subject: PostMember) = updateCount(subject, "postsCount", 1)
    fun decrementPostsCount(subject: PostMember) = updateCount(subject, "postsCount", -1)
    fun incrementPostCommentsCount(subject: PostMember) = updateCount(subject, "postCommentsCount", 1)
    fun decrementPostCommentsCount(subject: PostMember) = updateCount(subject, "postCommentsCount", -1)

    fun getPostsCount(subject: PostMember) = getCount(subject, "postsCount")
    fun getPostCommentsCount(subject: PostMember) = getCount(subject, "postCommentsCount")
}
