package todoservice.todo

import java.util.UUID

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
