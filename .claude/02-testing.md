## Testing

### Framework & tools
- **JUnit Jupiter** (`org.junit.jupiter.api.*`) drives tests: `@Test`, `@BeforeEach`.
- **AssertJ** for all assertions: `org.assertj.core.api.Assertions.assertThat` (bundled with `spring-boot-starter-test`) — never `org.junit.jupiter.api.Assertions.*` or Hamcrest. Common forms: `assertThat(actual).isEqualTo(expected)`, `.isTrue()`/`.isFalse()`, `.isInstanceOf(T.class)`, `.isInstanceOfSatisfying(T.class, result -> …)` when the cast value is asserted on, `.hasSize(n)`, `.isEmpty()`, `.isNotBlank()`, `.contains(x)`, `.anyMatch(item -> …)`. MockMvc `andExpect(status()…)` / `andExpect(jsonPath(…))` result matchers are the integration-test HTTP mechanism, not assertion-library calls, and stay as-is.
- **Mockito** for mocking (bundled with `spring-boot-starter-test`): plain `mock()` / `@Mock` for collaborators whose return is irrelevant; explicit `when(...).thenReturn(...)` / `verify(...)` for the behaviour under test. To echo a saved argument through a generic method, `when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0))`.
- **Integration tests**: `@SpringBootTest` + Testcontainers (`@ServiceConnection`, real backing services), MockMvc for HTTP. A library module needs a test-only `@SpringBootApplication` to bootstrap its slice. Build request bodies from the real DTO serialized with the injected Jackson `ObjectMapper` (`writeValueAsString`) — never hand-written JSON strings.

### Structure — Given / When / Then
- Each test body is three blank-line-separated sections — arrange, act, assert — ideally three lines.
- Extract all mechanics into intent-named helpers: `given…()` for stubbing **and** test-data builders, `then…()` for assertions. The body reads as prose; no inline setup or asserts.
- Name the act's result `actual…` (e.g. `actualSignUpResult`).
- **Define helpers in order of use**: each helper sits immediately after the first `@Test` that uses it, so the file reads top-down — a test, then its helpers, then the next test and the new helpers it introduces.

### Granularity & naming
- **One behaviour per `@Test`.** Split outcomes into separate tests — *returns result*, *publishes event*, *does NOT publish on failure*, *persists entities* — rather than multiple asserts in one test.
- Java has no backtick identifiers: encode the **behaviour + condition** in a `camelCase` method name; no `@DisplayName`. Describe behaviour, not the method under test — `theEventPublisherIsNotCalledWhenAnEmailIsAlreadyRegistered()`.

### Shape
```java
@Test
void rejectsDuplicateEmailAndReturnsEmailAlreadyRegistered() {
    givenEmailAlreadyRegistered();

    var actualSignUpResult = service.signUp(givenSignUpCommand());

    thenEmailAlreadyRegisteredIsReturned(actualSignUpResult);
}
```
