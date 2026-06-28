FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /src

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY shared/pom.xml shared/
COPY parties/pom.xml parties/
COPY addresses/pom.xml addresses/
COPY capabilities/pom.xml capabilities/
COPY relationships/pom.xml relationships/
COPY bootstrap/pom.xml bootstrap/
RUN ./mvnw -B -q -pl bootstrap -am dependency:go-offline

COPY shared/src shared/src
COPY parties/src parties/src
COPY addresses/src addresses/src
COPY capabilities/src capabilities/src
COPY relationships/src relationships/src
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
