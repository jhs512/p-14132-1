package com.back.boundedContexts.post.postMember.service

import com.back.boundedContexts.post.app.PostMemberAttrService
import com.back.boundedContexts.post.app.PostMemberService
import com.back.standard.extensions.getOrThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostMemberAttrServiceTest {
    @Autowired
    private lateinit var postMemberService: PostMemberService

    @Autowired
    private lateinit var postMemberAttrService: PostMemberAttrService

    @Test
    @DisplayName("postsCount 증가")
    fun t1() {
        val postUser1 = postMemberService.findByUsername("user1").getOrThrow()
        postMemberAttrService.incrementPostsCount(postUser1)
    }

    @Test
    @DisplayName("postsCount 감소")
    fun t2() {
        val postUser1 = postMemberService.findByUsername("user1").getOrThrow()
        postMemberAttrService.decrementPostsCount(postUser1)
    }

    @Test
    @DisplayName("postCommentsCount 증가")
    fun t3() {
        val postUser1 = postMemberService.findByUsername("user1").getOrThrow()
        postMemberAttrService.incrementPostCommentsCount(postUser1)
    }

    @Test
    @DisplayName("postCommentsCount 감소")
    fun t4() {
        val postUser1 = postMemberService.findByUsername("user1").getOrThrow()
        postMemberAttrService.decrementPostCommentsCount(postUser1)
    }

    @Test
    @DisplayName("postsCount 조회")
    fun t5() {
        val postUser1 = postMemberService.findByUsername("user1").getOrThrow()
        postMemberAttrService.getPostsCount(postUser1)
    }

    @Test
    @DisplayName("postCommentsCount 조회")
    fun t6() {
        val postUser1 = postMemberService.findByUsername("user1").getOrThrow()
        postMemberAttrService.getPostCommentsCount(postUser1)
    }
}