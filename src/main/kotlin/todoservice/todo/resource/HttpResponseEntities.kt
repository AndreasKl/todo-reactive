package todoservice.todo.resource

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ResponseStatus

object HttpResponseEntities {

    fun notFound(): ResponseEntity<*> =
        ResponseEntity.notFound().build<Void>()

    fun noContent(): ResponseEntity<Void> =
        ResponseEntity.status(HttpStatus.NO_CONTENT).build()

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class NotFoundException(message: String) : RuntimeException(message)

}