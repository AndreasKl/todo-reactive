package todoservice.todo

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import todoservice.todo.persistence.TaskEntity
import todoservice.todo.persistence.TodoEntity
import java.util.UUID

@Component
class TodoUseCases(
    private val taskRepository: TaskRepository,
    private val todoRepository: TodoRepository
) {

    // FIXME: Consider moving the mapping code where it belongs to.

    fun findAll(): Flux<Todo> {
        return todoRepository.findAll().flatMap { task ->
            taskRepository.findByTodoId(task.id)
                .collectList().map { mapToModel(task, it) }
        }
    }

    fun findById(id: UUID): Mono<Todo> {
        return todoRepository.findById(id).flatMap { task ->
            taskRepository.findByTodoId(task.id)
                .collectList().map { mapToModel(task, it) }
        }
    }

    fun createTodo(todo: Todo): Mono<Todo> {
        val save = todoRepository.save(mapToEntity(todo))
        return save.flatMap { saved ->
            val tasks = todo.tasks.map { mapToEntity(saved, it) }
            taskRepository.saveAll(tasks).collectList()
                .map { mapToModel(saved, it) }
        }
    }

    private fun mapToModel(todo: TodoEntity, tasks: List<TaskEntity>) =
        Todo(
            id = todo.id,
            name = todo.name,
            description = todo.description,
            tasks = mapToModel(tasks)
        )

    private fun mapToModel(tasks: List<TaskEntity>) =
        tasks.map { mapToModel(it) }

    private fun mapToModel(it: TaskEntity) =
        Task(id = it.id, name = it.name)

    private fun mapToEntity(todo: Todo) =
        TodoEntity(id = todo.id, name = todo.name, description = todo.description)

    private fun mapToEntity(todo: TodoEntity, task: Task) =
        TaskEntity(id = task.id, todoId = todo.id, name = task.name)


    data class Todo(
        val id: UUID = UUID.randomUUID(),
        val name: String,
        val description: String,
        val tasks: List<Task>
    )

    data class Task(
        val id: UUID = UUID.randomUUID(),
        val name: String,
    )
}
