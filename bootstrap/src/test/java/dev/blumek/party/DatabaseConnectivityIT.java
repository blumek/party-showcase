package dev.blumek.party;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("jdbc")
@Import(PostgresContainerSupport.class)
class DatabaseConnectivityIT {

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void reachesThePostgresContainer() {
        var actual = jdbcClient.sql("select 1").query(Integer.class).single();

        assertThat(actual).isEqualTo(1);
    }
}
