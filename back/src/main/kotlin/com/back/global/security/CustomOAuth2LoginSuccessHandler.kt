package com.back.global.security

import com.back.global.rq.Rq
import com.back.boundedContexts.sharedContexts.member.app.ActorFacade
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CustomOAuth2LoginSuccessHandler(
    private val actorFacade: ActorFacade,
    private val rq: Rq,
) : AuthenticationSuccessHandler {

    @Transactional(readOnly = true) // 이걸 안하면 actor.apiKey 에서 LazyInitializationException 발생
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val actor = rq.actor

        val accessToken = actorFacade.genAccessToken(actor)

        rq.setCookie("apiKey", actor.apiKey)
        rq.setCookie("accessToken", accessToken)

        val state = OAuth2State.decode(request.getParameter("state"))
        rq.sendRedirect(state.redirectUrl)
    }
}