package todoservice.todo

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import todoservice.todo.persistence.TodoEntity
import java.util.UUID


interface TodoRepository : ReactiveCrudRepository<TodoEntity, UUID> {
    fun findBy(pageable: Pageable): Flux<TodoEntity>
}