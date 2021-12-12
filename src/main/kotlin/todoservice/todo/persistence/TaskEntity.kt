package todoservice.todo.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("task")
data class TaskEntity(
    @Id val id: UUID = UUID.randomUUID(),
    val todoId: UUID,
    val name: String
) {
    @Version
    var version: Long = 0
}