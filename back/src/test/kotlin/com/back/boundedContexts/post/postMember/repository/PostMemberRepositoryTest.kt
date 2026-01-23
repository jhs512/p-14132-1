package com.back.boundedContexts.post.postMember.repository

import com.back.boundedContexts.post.domain.PostMemberProxy
import com.back.boundedContexts.post.out.PostMemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PostMemberRepositoryTest {
    @Autowired
    private lateinit var postMemberRepository: PostMemberRepository

    @Test
    @DisplayName("PostUserProxy")
    fun t1() {
        val realPostUser = postMemberRepository.getReferenceById(1)

        val postUser = PostMemberProxy(
            realPostUser,
            1,
            "system",
            "시스템",
        )

        // fulfill 필요없음
        assertThat(postUser.id).isEqualTo(1)
        assertThat(postUser.username).isEqualTo("system")
        assertThat(postUser.name).isEqualTo("시스템")
        assertThat(postUser.redirectToProfileImgUrlOrDefault).endsWith("/api/v1/members/1/redirectToProfileImg")
        assertThat(postUser.isAdmin).isEqualTo(true)
        assertThat(postUser.authorities).hasSize(1)
        assertThat(postUser.authoritiesAsStringList).containsExactly("ROLE_ADMIN")
        assertThat(postUser).isEqualTo(realPostUser)

        // fulfill 필요함
        assertThat(postUser.createDate).isNotNull
        assertThat(postUser.modifyDate).isNotNull
        assertThat(postUser.profileImgUrl).isBlank
        assertThat(postUser.postsCount).isNotNull
        assertThat(postUser.postCommentsCount).isNotNull
    }
}