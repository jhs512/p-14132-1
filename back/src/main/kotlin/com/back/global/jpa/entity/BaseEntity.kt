package com.back.global.jpa.entity

import com.back.sharedContexts.member.domain.Member
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Member) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}