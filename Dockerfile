FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /src

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY shared/pom.xml shared/
COPY parties/pom.xml parties/
COPY parties/domain/pom.xml parties/domain/
COPY parties/application/pom.xml parties/application/
COPY parties/adapter-jdbc/pom.xml parties/adapter-jdbc/
COPY parties/adapter-web/pom.xml parties/adapter-web/
COPY addresses/pom.xml addresses/
COPY addresses/domain/pom.xml addresses/domain/
COPY addresses/application/pom.xml addresses/application/
COPY addresses/adapter-jdbc/pom.xml addresses/adapter-jdbc/
COPY addresses/adapter-web/pom.xml addresses/adapter-web/
COPY capabilities/pom.xml capabilities/
COPY capabilities/domain/pom.xml capabilities/domain/
COPY capabilities/application/pom.xml capabilities/application/
COPY capabilities/adapter-jdbc/pom.xml capabilities/adapter-jdbc/
COPY capabilities/adapter-web/pom.xml capabilities/adapter-web/
COPY relationships/pom.xml relationships/
COPY relationships/domain/pom.xml relationships/domain/
COPY relationships/application/pom.xml relationships/application/
COPY relationships/adapter-jdbc/pom.xml relationships/adapter-jdbc/
COPY relationships/adapter-web/pom.xml relationships/adapter-web/
COPY bootstrap/pom.xml bootstrap/
RUN ./mvnw -B -q -pl bootstrap -am dependency:go-offline

COPY shared/src shared/src
COPY parties/domain/src parties/domain/src
COPY parties/application/src parties/application/src
COPY parties/adapter-jdbc/src parties/adapter-jdbc/src
COPY parties/adapter-web/src parties/adapter-web/src
COPY addresses/domain/src addresses/domain/src
COPY addresses/application/src addresses/application/src
COPY addresses/adapter-jdbc/src addresses/adapter-jdbc/src
COPY addresses/adapter-web/src addresses/adapter-web/src
COPY capabilities/domain/src capabilities/domain/src
COPY capabilities/application/src capabilities/application/src
COPY capabilities/adapter-jdbc/src capabilities/adapter-jdbc/src
COPY capabilities/adapter-web/src capabilities/adapter-web/src
COPY relationships/domain/src relationships/domain/src
COPY relationships/application/src relationships/application/src
COPY relationships/adapter-jdbc/src relationships/adapter-jdbc/src
COPY relationships/adapter-web/src relationships/adapter-web/src
COPY bootstrap/src bootstrap/src
RUN ./mvnw -B -pl bootstrap -am package -DskipTests \
 && cp bootstrap/target/bootstrap-*.jar application.jar \
 && java -Djarmode=tools -jar application.jar extract --layers --destination /extracted

FROM eclipse-temurin:25-jre-alpine AS run
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app

COPY --from=build /extracted/dependencies/ ./
COPY --from=build /extracted/spring-boot-loader/ ./
COPY --from=build /extracted/snapshot-dependencies/ ./
COPY --from=build /extracted/application/ ./

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]
