package todoservice.todo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import reactor.test.StepVerifier
import todoservice.support.EmbeddedPostgresExtension
import todoservice.todo.persistence.TodoEntity

private val aTodo = TodoEntity(name = "__name__", description = "__description__")

@SpringBootTest
@ExtendWith(EmbeddedPostgresExtension::class)
@ActiveProfiles("test")
class TodoRepositoryIntegrationTest {

    @Test
    fun savesAndLoads(@Autowired todoRepository: TodoRepository) {

        todoRepository.save(aTodo)
            .`as`(StepVerifier::create)
            .assertNext {
                assertThat(it.name).isEqualTo("__name__")
            }
            .verifyComplete()
    }

}