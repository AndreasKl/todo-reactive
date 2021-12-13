package todoservice.todo.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("todo")
data class TodoEntity(
    @Id val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    @Version var version : Long = 0
)

