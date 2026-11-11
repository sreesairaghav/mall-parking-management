# Optimised Dockerfile (branch: dockerfileopt)
#
# What changed compared with main:
#  1. Custom Java runtime built with jlink, containing only the JDK modules
#     this app uses, instead of the full Temurin JRE.
#  2. Runtime base is plain alpine (~8 MB) instead of eclipse-temurin:25-jre-alpine.
#  3. Spring Boot layered JAR: dependencies, loader and app code are separate
#     layers, so a code change only re-pushes the small application layer.
#  4. Tighter .dockerignore, so less build context is sent to Docker.

# ---------- Stage 1: build the JAR and a minimal JRE ----------
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

# Maven wrapper and pom first, so dependencies are cached until pom.xml changes
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q dependency:go-offline

COPY src/ src/

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q clean package -DskipTests \
 && cp target/*.jar app.jar \
 && java -Djarmode=tools -jar app.jar extract --layers --destination extracted \
 && mkdir -p extracted/dependencies extracted/spring-boot-loader \
             extracted/snapshot-dependencies extracted/application

# Find the JDK modules the app needs (falls back to a known-good list)
RUN DEPS=$(jdeps --ignore-missing-deps -q --recursive --multi-release 25 \
            --print-module-deps \
            --class-path 'extracted/dependencies/lib/*' \
            extracted/application/app.jar) \
    || DEPS="java.base,java.compiler,java.desktop,java.instrument,java.management,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.sql.rowset,jdk.jfr,jdk.unsupported" ; \
    echo "Modules: ${DEPS}" \
 && jlink \
      --add-modules "${DEPS},java.naming,java.sql,jdk.management,jdk.zipfs" \
      --strip-debug \
      --no-man-pages \
      --no-header-files \
      --compress=zip-6 \
      --output /javaruntime

# ---------- Stage 2: minimal runtime image ----------
FROM alpine:3.22

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=build /javaruntime ${JAVA_HOME}

# Non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Least-changing layers first
COPY --from=build /app/extracted/dependencies/ ./
COPY --from=build /app/extracted/spring-boot-loader/ ./
COPY --from=build /app/extracted/snapshot-dependencies/ ./
COPY --from=build /app/extracted/application/ ./

USER spring

EXPOSE 8085

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
