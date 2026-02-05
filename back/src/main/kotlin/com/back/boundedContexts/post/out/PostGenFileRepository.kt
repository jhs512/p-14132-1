package com.back.boundedContexts.post.out

import com.back.boundedContexts.post.domain.PostGenFile
import org.springframework.data.jpa.repository.JpaRepository

interface PostGenFileRepository : JpaRepository<PostGenFile, Int>
