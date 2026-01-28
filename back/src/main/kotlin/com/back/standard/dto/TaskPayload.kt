package com.back.standard.dto

import com.back.global.dto.Payload
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Task의 페이로드를 나타내는 인터페이스.
 * 비동기로 처리할 작업의 데이터를 담는다.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
interface TaskPayload : Payload