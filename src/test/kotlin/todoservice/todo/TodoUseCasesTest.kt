package todoservice.todo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import todoservice.todo.persistence.TaskEntity
import todoservice.todo.persistence.TodoEntity
import java.util.UUID

private val id = UUID.randomUUID()
private val todoEntityA = TodoEntity(name = "__name_a__", description = "__description__")
private val todoEntityB = TodoEntity(name = "__name_b__", description = "__description__")

internal class TodoUseCasesTest {

    @Test
    fun findAll() {
        val todoUseCases = todoUseCases()
        val todos = todoUseCases.findAll(Pageable.unpaged()).`as`(StepVerifier::create)

        todos
            .assertNext { assertTodo("__name_a__", it) }
            .assertNext { assertTodo("__name_b__", it) }
            .verifyComplete()
    }

    @Test
    fun findById() {
        val todoUseCases = todoUseCases()
        val todo = todoUseCases.findById(id).`as`(StepVerifier::create)

        todo
            .assertNext { assertTodo("__name_a__", it) }
            .verifyComplete()
    }

    @Test
    fun findByIdMapsOnEmpty() {
        val todoUseCases = todoUseCases()
        val todo = todoUseCases.findById(UUID.randomUUID()).`as`(StepVerifier::create)

        todo.verifyComplete()
    }

    @Test
    fun createNewTodo() {
        val todoUseCases = todoUseCases()
        val todo = todoUseCases.save(
            Todo(
                name = "__todo__", description = "__todo_desc__", tasks = listOf(
                    Task(name = "__task__")
                )
            )
        ).`as`(StepVerifier::create)

        todo.expectNextCount(1).verifyComplete()
    }


    private fun todoUseCases() = TodoUseCases(todoRepository(), taskRepositoryWithTaskForEntityA())

    private fun assertTodo(name: String, it: Todo) {
        assertThat(it)
            .extracting(Todo::name, Todo::description)
            .containsExactly(name, "__description__")
    }

    private fun todoRepository(): TodoRepository {
        return mock {
            on { findBy(Pageable.unpaged()) } doReturn Flux.just(todoEntityA, todoEntityB)
            on { findById(any<UUID>()) } doReturn Mono.empty()
            on { findById(id) } doReturn Mono.just(todoEntityA)
            on { save(any()) } doReturn Mono.just(todoEntityA)
        }
    }

    private fun taskRepositoryWithTaskForEntityA(): TaskRepository {
        return mock {
            on { findByTodoId(todoEntityA.id) } doReturn Flux.just(
                TaskEntity(
                    todoId = todoEntityA.id,
                    name = "__task_name__"
                )
            )
            on { findByTodoId(todoEntityB.id) } doReturn Flux.just()
            on { saveAll(any<List<TaskEntity>>()) } doReturn Flux.just()
        }
    }

}