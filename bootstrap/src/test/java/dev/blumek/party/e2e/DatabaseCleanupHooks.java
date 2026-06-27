package dev.blumek.party.e2e;

import io.cucumber.java.Before;
import org.springframework.jdbc.core.simple.JdbcClient;

public class DatabaseCleanupHooks {

    private final JdbcClient jdbcClient;
    private final ScenarioContext context;

    public DatabaseCleanupHooks(final JdbcClient jdbcClient, final ScenarioContext context) {
        this.jdbcClient = jdbcClient;
        this.context = context;
    }

    @Before(order = 0)
    public void resetState() {
        context.reset();
        truncateAllTables();
    }

    private void truncateAllTables() {
        final var tables = jdbcClient.sql("""
                select tablename from pg_tables
                where schemaname = 'public' and tablename <> 'flyway_schema_history'
                """).query(String.class).list();
        if (tables.isEmpty()) {
            return;
        }
        jdbcClient.sql("truncate table " + String.join(", ", tables) + " restart identity cascade").update();
    }
}
