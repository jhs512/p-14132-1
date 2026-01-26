package com.back.boundedContexts.shared.task.`in`

import com.back.boundedContexts.shared.task.app.TaskHandlerRegistry
import com.back.boundedContexts.shared.task.domain.TaskPayload
import com.back.boundedContexts.shared.task.out.TaskRepository
import com.back.standard.util.Ut
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.Executors

@Component
class TaskProcessor(
    private val taskRepository: TaskRepository,
    private val taskHandlerRegistry: TaskHandlerRegistry,
    private val transactionTemplate: TransactionTemplate
) {
    private val logger = LoggerFactory.getLogger(TaskProcessor::class.java)
    private val executor = Executors.newFixedThreadPool(10)

    @Scheduled(fixedDelay = 1000)
    fun processTasks() {
        val taskIds = transactionTemplate.execute {
            val pendingTasks = taskRepository.findPendingTasksWithLock(10)
            pendingTasks.forEach { it.markProcessing() }
            pendingTasks.map { it.id }
        } ?: emptyList()

        taskIds.forEach { taskId ->
            executor.submit { executeTask(taskId) }
        }
    }

    private fun executeTask(taskId: Int) = transactionTemplate.execute {
        val task = taskRepository.findById(taskId).orElse(null) ?: return@execute

        try {
            val payloadClass = Class.forName(task.taskType)
            val payload = Ut.JSON.fromString(task.payload, payloadClass) as TaskPayload
            val handler = taskHandlerRegistry.getHandler(payload::class.java)

            if (handler != null) {
                handler.method.invoke(handler.bean, payload)
                task.markCompleted()
            } else {
                logger.warn("No handler found for task type: ${task.taskType}")
                task.errorMessage = "No handler found"
                task.scheduleRetry()
            }
        } catch (e: Exception) {
            val rootCause = e.cause ?: e
            logger.error("Task failed: $taskId (retry: ${task.retryCount}/${task.maxRetries})", rootCause)
            task.errorMessage = rootCause.message ?: rootCause::class.simpleName
            task.scheduleRetry()
        }
    }
}
