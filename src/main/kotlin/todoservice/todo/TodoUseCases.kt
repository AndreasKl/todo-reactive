package todoservice.todo

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import todoservice.todo.persistence.TaskEntity
import todoservice.todo.persistence.TodoEntity
import java.util.UUID

@Component
class TodoUseCases(
    private val todoRepository: TodoRepository,
    private val taskRepository: TaskRepository
) {

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Flux<Todo> {
        return todoRepository.findBy(pageable).flatMap { task ->
            taskRepository.findByTodoId(task.id)
                .collectList().map { mapToModel(task, it) }
        }
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): Mono<Todo> {
        return todoRepository.findById(id).flatMap { task ->
            taskRepository.findByTodoId(task.id)
                .collectList().map { mapToModel(task, it) }
        }
    }

    @Transactional
    fun save(todo: Todo): Mono<Todo> {
        return todoRepository.save(mapToEntity(todo))
            .flatMap { saved -> updateTasks(todo, saved) }
    }

    @Transactional
    fun update(todo: Todo): Mono<Todo> {
        return todoRepository.findById(todo.id).flatMap {
            todoRepository.save(updateEntity(it, todo)).flatMap { saved ->
                removeAndUpdateTasks(saved, todo)
            }
        }
    }

    @Transactional
    fun delete(id: UUID): Mono<Void> {
        return todoRepository.deleteById(id)
    }

    private fun removeAndUpdateTasks(
        saved: TodoEntity,
        todo: Todo
    ): Mono<Todo> {
        return taskRepository.deleteAllByTodoId(saved.id)
            .flatMap { updateTasks(todo, saved) }
    }

    private fun updateTasks(
        todo: Todo,
        saved: TodoEntity
    ): Mono<Todo> {
        val tasks = todo.tasks.map { mapToEntity(saved, it) }
        return taskRepository.saveAll(tasks).collectList()
            .map { mapToModel(saved, it) }
    }

    private fun updateEntity(saved: TodoEntity, new: Todo): TodoEntity {
        return saved.copy(name = new.name, description = new.description)
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

    private fun mapToModel(taskEntity: TaskEntity) =
        Task(id = taskEntity.id, name = taskEntity.name)

    private fun mapToEntity(todo: Todo) =
        TodoEntity(id = todo.id, name = todo.name, description = todo.description)

    private fun mapToEntity(todoEntity: TodoEntity, task: Task) =
        TaskEntity(id = task.id, todoId = todoEntity.id, name = task.name)

}
