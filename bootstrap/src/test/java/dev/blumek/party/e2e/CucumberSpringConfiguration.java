package dev.blumek.party.e2e;

import dev.blumek.party.PostgresContainerSupport;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("jdbc")
@Import(PostgresContainerSupport.class)
public class CucumberSpringConfiguration {
}
