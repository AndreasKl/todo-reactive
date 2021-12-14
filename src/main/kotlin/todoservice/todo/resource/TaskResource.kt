package todoservice.todo.resource

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import todoservice.todo.Task
import todoservice.todo.TaskUseCases
import todoservice.todo.resource.HttpResponseEntities.NotFoundException
import java.util.UUID

@RestController
@RequestMapping("/todos")
class TaskResource(private val taskUseCases: TaskUseCases) {

    @GetMapping("/{todoId}/tasks")
    fun list(@PathVariable todoId: UUID): Mono<ResponseEntity<*>> {
        return taskUseCases.findByTodo(todoId).collectList()
            .map { mapToTaskResponse(it) }
            .switchIfEmpty(Mono.error(NotFoundException("Unknown entity with todoId: $todoId")))
    }

    @GetMapping("/{todoId}/tasks/{taskId}")
    fun single(
        @PathVariable todoId: UUID,
        @PathVariable taskId: UUID
    ): Mono<ResponseEntity<*>> {
        return taskUseCases.find(taskId, todoId)
            .map { mapToTaskResponse(it) }
            .switchIfEmpty(Mono.error(NotFoundException("Unknown entity with todoId: $todoId and taskId: $taskId")))
    }

    @PostMapping("/{todoId}/tasks")
    fun add(
        @PathVariable todoId: UUID,
        @RequestBody taskRequest: TaskRequest
    ): Mono<ResponseEntity<*>> {
        return taskUseCases.add(todoId, Task(name = taskRequest.name))
            .map { mapToTaskResponse(it) }
    }

    @PutMapping("/{todoId}/tasks/{taskId}")
    fun update(
        @PathVariable todoId: UUID,
        @PathVariable taskId: UUID,
        @RequestBody taskRequest: TaskRequest
    ): Mono<ResponseEntity<*>> {
        return taskUseCases.update(taskId, todoId, Task(id = taskId, name = taskRequest.name))
            .map { mapToTaskResponse(it) }
            .switchIfEmpty(Mono.error(NotFoundException("Unknown entity with todoId: $todoId and taskId: $taskId")))
    }

    @DeleteMapping("/{todoId}/tasks/{taskId}")
    fun delete(
        @PathVariable todoId: UUID,
        @PathVariable taskId: UUID
    ): Mono<ResponseEntity<*>> {
        return taskUseCases.delete(taskId, todoId)
            .thenReturn(HttpResponseEntities.noContent())
    }

    private fun mapToTaskResponse(it: Task) =
        ResponseEntity.ok(TodoResource.TaskResponse(it.id, it.name)) as ResponseEntity<*>

    private fun mapToTaskResponse(tasks: List<Task>) =
        ResponseEntity.ok(tasks.map {
            TodoResource.TaskResponse(it.id, it.name)
        }) as ResponseEntity<*>

    data class TaskRequest(val name: String)

}