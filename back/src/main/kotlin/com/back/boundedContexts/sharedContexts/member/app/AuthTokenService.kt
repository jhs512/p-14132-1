package com.back.boundedContexts.sharedContexts.member.app

import com.back.boundedContexts.sharedContexts.member.domain.Member
import com.back.boundedContexts.sharedContexts.member.dto.AccessTokenPayload
import com.back.standard.util.Ut
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AuthTokenService(
    @param:Value("\${custom.jwt.secretKey}")
    private val jwtSecretKey: String,
    @param:Value("\${custom.accessToken.expirationSeconds}")
    private val accessTokenExpirationSeconds: Int
) {
    fun genAccessToken(member: Member): String =
        Ut.JWT.toString(
            jwtSecretKey,
            accessTokenExpirationSeconds,
            mapOf(
                "id" to member.id,
                "username" to member.username,
                "name" to member.name
            )
        )

    fun payload(accessToken: String): AccessTokenPayload? {
        val parsed = Ut.JWT.payload(jwtSecretKey, accessToken) ?: return null

        return AccessTokenPayload(
            id = parsed["id"] as Int,
            username = parsed["username"] as String,
            name = parsed["name"] as String
        )
    }
}