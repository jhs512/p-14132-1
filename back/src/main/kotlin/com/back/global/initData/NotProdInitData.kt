package com.back.global.initData

import com.back.boundedContexts.post.app.PostFacade
import com.back.boundedContexts.post.app.PostMemberService
import com.back.global.app.CustomConfigProperties
import com.back.shared.actor.app.ActorFacade
import com.back.standard.extensions.getOrThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional

@Profile("!prod")
@Configuration
class NotProdInitData(
    private val postFacade: PostFacade,
    private val actorFacade: ActorFacade,
    private val postMemberService: PostMemberService,
    private val customConfigProperties: CustomConfigProperties,
) {
    @Lazy
    @Autowired
    private lateinit var self: NotProdInitData

    @Bean
    fun notProdInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            self.work1()
            self.work2()
        }
    }

    @Transactional
    fun work1() {
        if (actorFacade.count() > 0) return

        val memberSystem = actorFacade.join("system", "1234", "시스템")
        memberSystem.modifyApiKey(memberSystem.username)

        val memberAdmin = actorFacade.join("admin", "1234", "관리자")
        memberAdmin.modifyApiKey(memberAdmin.username)

        val memberUser1 = actorFacade.join("user1", "1234", "유저1")
        memberUser1.modifyApiKey(memberUser1.username)

        val memberUser2 = actorFacade.join("user2", "1234", "유저2")
        memberUser2.modifyApiKey(memberUser2.username)

        val memberUser3 = actorFacade.join("user3", "1234", "유저3")
        memberUser3.modifyApiKey(memberUser3.username)

        // 코틀린 람다 스타일로 변경
        customConfigProperties.notProdMembers.forEach { notProdMember ->
            val socialMember = actorFacade.join(
                notProdMember.username,
                null,
                notProdMember.nickname,
                notProdMember.profileImgUrl
            )
            socialMember.modifyApiKey(notProdMember.apiKey)
        }
    }

    @Transactional
    fun work2() {
        if (postFacade.count() > 0) return

        val postUser1 = postMemberService.findByUsername("user1").getOrThrow()
        val postUser2 = postMemberService.findByUsername("user2").getOrThrow()
        val postUser3 = postMemberService.findByUsername("user3").getOrThrow()

        val post1 = postFacade.write(postUser1, "제목 1", "내용 1")
        val post2 = postFacade.write(postUser1, "제목 2", "내용 2")
        val post3 = postFacade.write(postUser2, "제목 3", "내용 3")

        postFacade.writeComment(postUser1, post1, "댓글 1-1")
        postFacade.writeComment(postUser1, post1, "댓글 1-2")
        postFacade.writeComment(postUser2, post1, "댓글 1-3")
        postFacade.writeComment(postUser3, post2, "댓글 2-1")
        postFacade.writeComment(postUser3, post2, "댓글 2-2")
    }
}