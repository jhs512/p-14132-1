package com.back.boundedContexts.shared.task.domain

/**
 * Task 핸들러 메서드를 표시하는 어노테이션.
 * TaskPayload 타입의 파라미터 하나를 받는 메서드에 사용한다.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskHandler
