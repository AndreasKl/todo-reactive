package todoservice.todo

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import todoservice.todo.persistence.TodoEntity
import java.util.UUID

interface TodoRepository : ReactiveCrudRepository<TodoEntity, UUID>