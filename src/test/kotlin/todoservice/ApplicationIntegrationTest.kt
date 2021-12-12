package todoservice

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import todoservice.support.EmbeddedPostgresExtension

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(EmbeddedPostgresExtension::class)
class ApplicationIntegrationTest {

    @Test
    fun applicationStarts() {
    }

}
