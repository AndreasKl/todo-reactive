package todoservice.todo.resource

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import todoservice.support.EmbeddedPostgresExtension
import java.net.URI
import java.util.UUID

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(EmbeddedPostgresExtension::class)
class TaskResourceIntegrationTest(@Autowired private val webTestClient: WebTestClient) {

    @Test
    fun fetchingList() {
        webTestClient.get().uri("/todos").exchange()
            .expectStatus().isOk
            .expectBody<String>().returnResult()
    }

    @Test
    fun fetchUnknownByIdAndExpectNotFound() {
        webTestClient.get().uri("/todos/aa2b2316-5c56-11ec-bf63-0242ac130002").exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun createAndFetchById() {
        val returnResult = postTodoWithTask()
        val location = returnResult.responseHeaders.location!!

        getTodoFormLocation(location)
    }

    @Test
    fun createAndDeleteById() {
        val returnResult = postTodoWithTask()
        val location = returnResult.responseHeaders.location!!

        val getResponse = getTodoFormLocation(location)
        val get = JSONObject(getResponse.responseBody)
        val id = get.getString("id")

        deleteTodo(UUID.fromString(id))

        webTestClient.get().uri("/todos/$id").exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun createUpdateAndFetchById() {
        val returnResult = postTodoWithTask()
        val location = returnResult.responseHeaders.location!!

        val getResponse = getTodoFormLocation(location)
        val get = JSONObject(getResponse.responseBody)
        assertThat(get["name"]).isEqualTo("__name__")

        val id = get.getString("id")

        val putResponse = putTodoWithTask(UUID.fromString(id))
        val put = JSONObject(putResponse.responseBody)
        assertThat(put["name"]).isEqualTo("__updated_name__")
    }

    @Test
    fun updateUnknownTodo() {
        webTestClient.put().uri("/todos/{id}", UUID.randomUUID())
            .contentType(APPLICATION_JSON)
            .bodyValue(payload("__updated_name__")).exchange()
            .expectStatus().isNotFound
    }

    private fun getTodoFormLocation(location: URI) =
        webTestClient.get().uri(location).exchange()
            .expectStatus().isOk
            .expectBody<String>().returnResult()

    private fun postTodoWithTask() =
        webTestClient.post().uri("/todos")
            .contentType(APPLICATION_JSON)
            .bodyValue(payload("__name__")).exchange()
            .expectStatus().isCreated
            .expectHeader().exists("location")
            .expectBody<String>().returnResult()

    private fun putTodoWithTask(id: UUID) =
        webTestClient.put().uri("/todos/{id}", id)
            .contentType(APPLICATION_JSON)
            .bodyValue(payload("__updated_name__")).exchange()
            .expectStatus().isOk
            .expectBody<String>().returnResult()

    private fun deleteTodo(id: UUID) =
        webTestClient.delete().uri("/todos/{id}", id)
            .exchange()
            .expectStatus().isNoContent

    private fun payload(name: String): String {
        return """
            {
                "name": "$name",
                "description": "__todo_description__",
                "tasks": [
                    {
                        "name": "__task_name__"
                    }
                ]
            }
            """
    }

}
