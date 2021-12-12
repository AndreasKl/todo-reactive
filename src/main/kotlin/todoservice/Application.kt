package todoservice

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import java.nio.charset.StandardCharsets.UTF_8
import java.util.TimeZone

private val logger = KotlinLogging.logger {}

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
class Application

fun main(args: Array<String>) {
    prepareStage()
    runApplication<Application>(*args)
}

internal fun prepareStage() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    System.setProperty("file.encoding", UTF_8.name())

    Thread.setDefaultUncaughtExceptionHandler { t: Thread, e: Throwable ->
        logger.error(e) {
            "Uncaught exception occurred on thread ${t.name}."
        }
    }
}