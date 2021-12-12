package todoservice.support

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension
import com.opentable.db.postgres.junit5.PreparedDbExtension
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class EmbeddedPostgresExtension : BeforeAllCallback, AfterAllCallback {

    private val preparedDatabase = preparedDatabase()

    override fun afterAll(context: ExtensionContext?) {
        preparedDatabase.afterAll(context)
    }

    override fun beforeAll(context: ExtensionContext?) {
        preparedDatabase.beforeAll(context)
    }

    private fun preparedDatabase(): PreparedDbExtension =
        EmbeddedPostgresExtension
            .preparedDatabase { ds ->
                ds.connection.use { connection ->
                    connection.prepareStatement("CREATE DATABASE todo").use { ps -> ps.execute() }
                }
            }
            .customize { customizer ->
                customizer.setPort(54321)
            }

}