package todoservice.todo.resource

import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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

    // FIXME: To be implemented
    //
    // Validation...
    //
    // add task
    // -> /todos/uuid/tasks/     || POST
    // update task
    // -> /todos/uuid/tasks/uuid || PUT
    // remove task
    // -> /todos/uuid/tasks/uuid || DELETE
    //
    // Icing...
    // open-api
    // structure polish

    @GetMapping
    fun list(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
    ): Flux<TodoResponse> {
        return todoUseCases.findAll(PageRequest.of(page, size))
            .map(this::mapToResponse)
    }

    @GetMapping("/{id}")
    fun single(@PathVariable id: UUID): Mono<ResponseEntity<*>> {
        return todoUseCases.findById(id)
            .map(this::mapToResponse).map { ok(it) }
            .switchIfEmpty(notFound())
    }


    @PostMapping
    fun create(
        @RequestBody request: TodoRequest,
        componentsBuilder: UriComponentsBuilder
    ): Mono<ResponseEntity<*>> {
        return todoUseCases.save(mapToModel(request))
            .map(this::mapToResponse)
            .map { created(componentsBuilder, it) }
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: TodoRequest
    ): Mono<ResponseEntity<*>> {
        return todoUseCases.update(mapToModel(id, request))
            .map(this::mapToResponse)
            .map { updated(it) }
            .switchIfEmpty(notFound())
    }

    private fun mapToModel(id: UUID, request: TodoRequest) =
        mapToModel(request).copy(id = id)

    private fun mapToModel(request: TodoRequest) =
        Todo(
            name = request.name,
            description = request.description,
            tasks = mapToModel(request.tasks)
        )

    private fun mapToModel(tasks: List<TaskRequest>) =
        tasks.map { Task(name = it.name) }

    private fun mapToResponse(todo: Todo): TodoResponse =
        TodoResponse(
            id = todo.id,
            name = todo.name,
            description = todo.description,
            mapToResponse(todo.tasks)
        )

    private fun mapToResponse(tasks: List<Task>) =
        tasks.map { TaskResponse(id = it.id, name = it.name) }

    private fun created(
        componentsBuilder: UriComponentsBuilder,
        todoResponse: TodoResponse
    ): ResponseEntity<*> =
        ResponseEntity.created(createdPath(componentsBuilder, todoResponse)).body(todoResponse)

    private fun createdPath(componentsBuilder: UriComponentsBuilder, todoResponse: TodoResponse) =
        componentsBuilder.path("/todos/{id}").buildAndExpand(todoResponse.id).toUri()

    private fun updated(todoResponse: TodoResponse): ResponseEntity<*> =
        ResponseEntity.ok().body(todoResponse)

    private fun ok(todoResponse: TodoResponse): ResponseEntity<*> =
        ResponseEntity.ok(todoResponse)

    private fun notFound(): Mono<ResponseEntity<*>> =
        Mono.just(ResponseEntity.notFound().build<Void>())

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
