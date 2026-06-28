package dev.blumek.party;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("jdbc")
@Import(PostgresContainerSupport.class)
class PartyShowcaseApplicationIT {

    @Test
    void contextLoads() {
    }
}
