package todoservice

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.TimeZone

class ApplicationTest {

    @Test
    fun `app should always use UTC and UTF-8`() {
        System.setProperty("file.encoding", StandardCharsets.US_ASCII.name())
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"))

        prepareStage()

        assertThat(TimeZone.getDefault()).isEqualTo(TimeZone.getTimeZone("UTC"))
        assertThat(Charset.defaultCharset()).isEqualTo(StandardCharsets.UTF_8);
    }

}