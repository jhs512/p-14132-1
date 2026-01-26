package com.back.boundedContexts.shared.task.app

import com.back.boundedContexts.shared.task.domain.Task
import com.back.boundedContexts.shared.task.domain.TaskPayload
import com.back.boundedContexts.shared.task.out.TaskRepository
import com.back.standard.util.Ut
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository
) {
    fun add(payload: TaskPayload) {
        taskRepository.save(
            Task(
                uid = UUID.randomUUID(),
                taskType = payload::class.java.name,
                payload = Ut.JSON.toString(payload)
            )
        )
    }
}
