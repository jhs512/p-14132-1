package com.back.global.task.app

import com.back.global.task.domain.Task
import com.back.global.task.out.TaskRepository
import com.back.standard.dto.TaskPayload
import com.back.standard.util.Ut
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskFacade(
    private val taskRepository: TaskRepository
) {
    fun add(payload: TaskPayload) {
        taskRepository.save(
            Task(
                UUID.randomUUID(),
                payload.aggregateType,
                payload.aggregateId,
                payload::class.java.name,
                Ut.JSON.toString(payload)
            )
        )


    }
}
