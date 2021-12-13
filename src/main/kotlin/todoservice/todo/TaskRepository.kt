package todoservice.todo

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import todoservice.todo.persistence.TaskEntity
import java.util.UUID

interface TaskRepository : ReactiveCrudRepository<TaskEntity, UUID> {
    fun findByTodoId(todoId : UUID) : Flux<TaskEntity>
    fun deleteAllByTodoId(todoId : UUID) : Mono<Boolean>
}