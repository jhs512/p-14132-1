package com.back.boundedContexts.post.postMember.repository

import com.back.boundedContexts.post.domain.PostMemberAttr
import com.back.boundedContexts.post.out.PostUserAttrRepository
import com.back.boundedContexts.post.out.PostMemberRepository
import com.back.standard.extensions.getOrThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PostMemberAttrRepositoryTest {
    @Autowired
    private lateinit var postMemberRepository: PostMemberRepository

    @Autowired
    private lateinit var postUserAttrRepository: PostUserAttrRepository

    @Test
    @DisplayName("saveInt")
    fun t1() {
        val postUserSystem = postMemberRepository.findByUsername("system").getOrThrow()

        val attr = PostMemberAttr(
            postUserSystem,
            "postsCount",
            0.toString()
        )

        postUserAttrRepository.save(attr)
    }

    @Test
    @DisplayName("saveString")
    fun t2() {
        val postUserSystem = postMemberRepository.findByUsername("system").getOrThrow()

        val attr = PostMemberAttr(
            postUserSystem,
            "grade",
            "브론즈"
        )

        postUserAttrRepository.save(attr)
    }

    @Test
    @DisplayName("find")
    fun t3() {
        val postUserSystem = postMemberRepository.findByUsername("system").getOrThrow()

        postUserAttrRepository.findBySubjectAndName(postUserSystem, "postsCount")
    }
}