package com.back.global.dto

import java.util.*

interface Payload {
    val uid: UUID
    val aggregateType: String
    val aggregateId: Int
}