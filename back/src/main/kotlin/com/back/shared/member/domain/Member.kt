package com.back.shared.member.domain

import com.back.global.jpa.entity.BaseTime
import com.back.shared.member.out.MemberAttrRepository
import jakarta.persistence.Column
import jakarta.persistence.Entity
import org.hibernate.annotations.NaturalId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

@Entity
class Member(
    id: Int,
    @field:NaturalId
    @field:Column(unique = true)
    val username: String,
    var password: String? = null,
    var nickname: String,
    @field:Column(unique = true) var apiKey: String,
) : BaseTime(id) {

    // ================================
    // Companion Object
    // ================================

    companion object {
        lateinit var attrRepository: MemberAttrRepository
    }

    // ================================
    // Constructors
    // ================================

    // 코프링에서 엔티티의 `by lazy` 필드가 제대로 작동하게 하려면
    // kotlin("plugin.jpa") 에 의해서 만들어지는 인자 없는 생성자로는 부족하다.
    // 귀찮지만 이렇게 직접 만들어야 한다.
    constructor() : this(0)

    constructor(id: Int) : this(id, "", "")

    constructor(id: Int, username: String, nickname: String) : this(
        id,
        username,
        null,
        nickname,
        ""
    )

    constructor(username: String, password: String?, nickname: String) : this(
        0,
        username,
        password,
        nickname,
        UUID.randomUUID().toString(),
    )

    // ================================
    // 공통 속성 (Profile)
    // ================================

    val name: String
        get() = nickname

    @delegate:Transient
    private val profileImgUrlAttr by lazy {
        attrRepository.findBySubjectAndName(this, "profileImgUrl")
            ?: MemberAttr(this, "profileImgUrl", "")
    }

    var profileImgUrl: String
        get() = profileImgUrlAttr.value
        set(value) {
            profileImgUrlAttr.value = value
            attrRepository.save(profileImgUrlAttr)
        }

    val profileImgUrlOrDefault: String
        get() = profileImgUrl
            .takeIf { it.isNotBlank() }
            ?: "https://placehold.co/600x600?text=U_U"

    @delegate:Transient
    val redirectToProfileImgUrlOrDefault: String by lazy {
        "http://localhost:8080/api/v1/members/${id}/redirectToProfileImg"
    }

    // ================================
    // Security 영역
    // ================================

    @delegate:Transient
    val isAdmin: Boolean by lazy {
        username in setOf("system", "admin")
    }

    @delegate:Transient
    val authoritiesAsStringList: List<String> by lazy {
        buildList { if (isAdmin) add("ROLE_ADMIN") }
    }

    @delegate:Transient
    val authorities: Collection<GrantedAuthority> by lazy {
        authoritiesAsStringList.map { SimpleGrantedAuthority(it) }
    }

    // ================================
    // PostMember 영역
    // ================================

    @delegate:Transient
    private val postsCountAttr by lazy {
        attrRepository.findBySubjectAndName(this, "postsCount")
            ?: MemberAttr(this, "postsCount", "0")
    }

    @delegate:Transient
    private val postCommentsCountAttr by lazy {
        attrRepository.findBySubjectAndName(this, "postCommentsCount")
            ?: MemberAttr(this, "postCommentsCount", "0")
    }

    var postsCount
        get() = postsCountAttr.value.toInt()
        set(value) {
            postsCountAttr.value = value.toString()
            attrRepository.save(postsCountAttr)
        }

    var postCommentsCount
        get() = postCommentsCountAttr.value.toInt()
        set(value) {
            postCommentsCountAttr.value = value.toString()
            attrRepository.save(postCommentsCountAttr)
        }

    fun incrementPostsCount() {
        postsCount++
    }

    fun decrementPostsCount() {
        postsCount--
    }

    fun incrementPostCommentsCount() {
        postCommentsCount++
    }

    fun decrementPostCommentsCount() {
        postCommentsCount--
    }

    // ================================
    // Member 전용 메서드
    // ================================

    fun modify(nickname: String, profileImgUrl: String?) {
        this.nickname = nickname
        profileImgUrl?.let { this.profileImgUrl = it }
    }

    fun modifyApiKey(apiKey: String) {
        this.apiKey = apiKey
    }
}
