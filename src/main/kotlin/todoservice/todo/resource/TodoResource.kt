package todoservice.todo.resource

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import todoservice.todo.TodoUseCases
import todoservice.todo.TodoUseCases.Task
import todoservice.todo.TodoUseCases.Todo
import java.util.UUID

@RestController
@RequestMapping("/todos")
class TodoResource(private val todoUseCases: TodoUseCases) {

    @GetMapping
    fun list(): Flux<TodoResponse> {
        return todoUseCases.findAll().map(this::mapToResponse)
    }

    @GetMapping("/{id}")
    fun single(@PathVariable id: UUID): Mono<TodoResponse> {
        return todoUseCases.findById(id).map(this::mapToResponse)
    }

    @PostMapping
    fun create(
        @RequestBody request: TodoRequest,
        componentsBuilder: UriComponentsBuilder
    ): Mono<ResponseEntity<TodoResponse>> {
        return todoUseCases.createTodo(mapToModel(request))
            .map(this::mapToResponse)
            .map { mapToCreated(componentsBuilder, it) }
    }

    // FIXME: To be implemented, end2end tests
    // Validation...
    //
    // update todo
    // -> /todos/uuid            || PUT
    // add task
    // -> /todos/uuid/tasks/     || POST
    // update task
    // -> /todos/uuid/tasks/uuid || PUT
    // remove task
    // -> /todos/uuid/tasks/uuid || DELETE

    private fun mapToModel(request: TodoRequest): Todo {
        return Todo(
            name = request.name,
            description = request.description,
            tasks = mapToModel(request.tasks)
        )
    }

    private fun mapToModel(tasks: List<TaskRequest>): List<Task> {
        return tasks.map { Task(name = it.name) }
    }

    private fun mapToResponse(todo: Todo): TodoResponse = TodoResponse(
        id = todo.id,
        name = todo.name,
        description = todo.description,
        mapToResponse(todo.tasks)
    )

    private fun mapToResponse(tasks: List<Task>): List<TaskResponse> =
        tasks.map { TaskResponse(id = it.id, name = it.name) }

    private fun mapToCreated(componentsBuilder: UriComponentsBuilder, it: TodoResponse) =
        ResponseEntity
            .created(componentsBuilder.path("/todos/{id}").buildAndExpand(it.id).toUri())
            .body(it)

    data class TodoResponse(
        val id: UUID,
        val name: String,
        val description: String,
        var tasks: List<TaskResponse>
    )

    data class TaskResponse(val id: UUID, val name: String)

    data class TodoRequest(
        val name: String,
        val description: String,
        var tasks: List<TaskRequest>
    )

    data class TaskRequest(val name: String)
}
