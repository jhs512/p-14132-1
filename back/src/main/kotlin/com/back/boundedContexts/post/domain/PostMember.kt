package com.back.boundedContexts.post.domain

import com.back.boundedContexts.post.out.PostUserAttrRepository
import com.back.shared.member.domain.BaseMember
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Immutable
@Entity
@Table(name = "member")
class PostMember(
    id: Int,
    username: String,
    @field:Column(name = "nickname") var nickname: String
) : BaseMember(id, username) {

    val name: String
        get() = nickname

    companion object {
        lateinit var attrRepository: PostUserAttrRepository
    }

    // 코프링에서 엔티티의 `by lazy` 필드가 제대로 작동하게 하려면
    // kotlin("plugin.jpa") 에 의해서 만들어지는 인자 없는 생성자로는 부족하다.
    // 귀찮지만 이렇게 직접 만들어야 한다.
    constructor() : this(0, "", "")

    @delegate:Transient
    private val postsCountAttr by lazy {
        attrRepository.findBySubjectAndName(this, "postsCount")
            ?: PostMemberAttr(this, "postsCount", "0")
    }

    @delegate:Transient
    private val postCommentsCountAttr by lazy {
        attrRepository.findBySubjectAndName(this, "postCommentsCount")
            ?: PostMemberAttr(this, "postCommentsCount", "0")
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
}