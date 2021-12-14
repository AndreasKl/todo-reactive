package todoservice.todo.resource

import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import todoservice.todo.Task
import todoservice.todo.Todo
import todoservice.todo.TodoUseCases
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/todos")
class TodoResource(private val todoUseCases: TodoUseCases) {

    // FIXME: To be implemented
    //
    // TEST: TaskResource
    // TEST: TaskRepository
    //
    // Validation
    // Paging literal
    // Icing
    // open-api
    // structure polish

    @GetMapping
    fun list(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
    ): Mono<TodosResponse> {
        return todoUseCases.findAll(PageRequest.of(page, size))
            .map(this::mapToResponse).collectList()
            .map { TodosResponse(it) }
    }

    @GetMapping("/{id}")
    fun single(@PathVariable id: UUID): Mono<ResponseEntity<*>> {
        return todoUseCases.findById(id)
            .map(this::mapToResponse).map { ok(it) }
            .switchIfEmpty(Mono.error(HttpResponseEntities.NotFoundException("Unknown entity with id: $id")))
    }

    @PostMapping
    fun create(
        @RequestBody request: TodoRequest,
        componentsBuilder: UriComponentsBuilder
    ): Mono<ResponseEntity<*>> {
        return todoUseCases.save(mapToModel(request))
            .map(this::mapToResponse)
            .map { created(it, createdPath(componentsBuilder, it)) }
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: TodoRequest
    ): Mono<ResponseEntity<*>> {
        return todoUseCases.update(mapToModel(id, request))
            .map(this::mapToResponse)
            .map { updated(it) }
            .switchIfEmpty(Mono.error(HttpResponseEntities.NotFoundException("Unknown entity with id: $id")))
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID
    ): Mono<ResponseEntity<Void>> {
        return todoUseCases.delete(id)
            .thenReturn(HttpResponseEntities.noContent())
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

    private fun created(todoResponse: TodoResponse, createdURI: URI): ResponseEntity<*> =
        ResponseEntity.created(createdURI).body(todoResponse)

    private fun createdPath(componentsBuilder: UriComponentsBuilder, todoResponse: TodoResponse) =
        componentsBuilder.path("/todos/{id}").buildAndExpand(todoResponse.id).toUri()

    private fun updated(todoResponse: TodoResponse): ResponseEntity<*> =
        ResponseEntity.ok().body(todoResponse)

    private fun ok(todoResponse: TodoResponse): ResponseEntity<*> =
        ResponseEntity.ok(todoResponse)

    data class TodosResponse(
        val todos: List<TodoResponse>
    )

    data class TodoResponse(
        val id: UUID,
        val name: String,
        val description: String,
        val tasks: List<TaskResponse>
    )

    data class TaskResponse(val id: UUID, val name: String)

    data class TodoRequest(
        val name: String,
        val description: String,
        var tasks: List<TaskRequest>
    )

    data class TaskRequest(val name: String)
}
