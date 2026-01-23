package com.back.shared.member.domain

import com.back.boundedContexts.member.domain.MemberAttr
import com.back.boundedContexts.member.out.MemberAttrRepository
import com.back.boundedContexts.member.out.MemberRepository
import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.NaturalId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@MappedSuperclass
class BaseMember(
    id: Int,
    @field:NaturalId
    @field:Column(unique = true)
    val username: String,
) : BaseTime(id) {
    companion object {
        lateinit var memberRepository: MemberRepository
        lateinit var memberAttrRepository: MemberAttrRepository
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is BaseMember) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    @delegate:Transient
    val profileImgUrlAttr by lazy {
        memberAttrRepository
            .findBySubjectAndName(
                memberRepository
                    .getReferenceById(this.id),
                "profileImgUrl"
            )
            ?: MemberAttr(memberRepository.getReferenceById(this.id), "profileImgUrl", "")
    }

    var profileImgUrl: String
        get() = profileImgUrlAttr.value
        set(value) {
            profileImgUrlAttr.value = value
            memberAttrRepository.save(profileImgUrlAttr)
        }

    val redirectToProfileImgUrlOrDefault: String
        get() = "http://localhost:8080/api/v1/members/${id}/redirectToProfileImg"

    val profileImgUrlOrDefault: String
        get() = profileImgUrl
            .takeIf { it.isNotBlank() }
            ?: "https://placehold.co/600x600?text=U_U"

    val isAdmin: Boolean
        get() = username in setOf("system", "admin")

    val authoritiesAsStringList: List<String>
        get() = buildList { if (isAdmin) add("ROLE_ADMIN") }

    val authorities: Collection<GrantedAuthority>
        get() = authoritiesAsStringList.map { SimpleGrantedAuthority(it) }
}