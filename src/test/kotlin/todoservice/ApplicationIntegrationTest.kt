package todoservice

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import todoservice.support.EmbeddedPostgresExtension

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(EmbeddedPostgresExtension::class)
class ApplicationIntegrationTest() {

    @Test
    fun applicationStarts() {
    }

}
