package com.back.boundedContexts.post.domain

class PostMemberProxy(
    private val real: PostMember,
    id: Int,
    username: String,
    name: String
) : PostMember(id, username, name) {
    override var createDate
        get() = real.createDate
        set(value) {
            real.createDate = value
        }

    override var modifyDate
        get() = real.modifyDate
        set(value) {
            real.modifyDate = value
        }

    override var postsCount
        get() = real.postsCount
        set(value) {
            real.postsCount = value
        }

    override var postCommentsCount
        get() = real.postCommentsCount
        set(value) {
            real.postCommentsCount = value
        }
}
