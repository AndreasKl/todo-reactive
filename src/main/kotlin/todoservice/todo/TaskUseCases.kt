package todoservice.todo

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import todoservice.todo.persistence.TaskEntity
import java.util.UUID

@Component
class TaskUseCases(private val taskRepository: TaskRepository) {

    fun findByTodo(todoId: UUID): Flux<Task> {
        return taskRepository.findByTodoId(todoId).map { Task(it.id, it.name) }
    }

    fun find(id: UUID, todoId: UUID): Mono<Task> {
        return taskRepository.findByIdAndTodoId(id, todoId).map { Task(it.id, it.name) }
    }

    fun add(todoId: UUID, task: Task): Mono<Task> {
        return taskRepository.save(TaskEntity(todoId = todoId, name = task.name))
            .map { Task(it.id, it.name) }
    }

    fun update(id: UUID, todoId: UUID, task: Task): Mono<Task> {
        if (task.id != id) {
            return Mono.error(IllegalArgumentException("task#id and id parameter do not match"))
        }

        return taskRepository.findByIdAndTodoId(id, todoId).flatMap { entity ->
            taskRepository.save(entity.copy(name = task.name))
                .map { Task(it.id, it.name) }
        }
    }

    fun delete(id: UUID, todoId: UUID): Mono<Void> {
        return taskRepository.deleteByIdAndTodoId(id, todoId)
    }

}
