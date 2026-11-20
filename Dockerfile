# ============================================================================
# Dockerfile - Servicios web SGFP (GA7-220501096-AA5-EV03)
# Construye la API con Maven y la ejecuta con Java 17. Usado por Render.
# ============================================================================

# ---- Etapa 1: compilar el proyecto y generar el .jar ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
# Primero solo el pom.xml para aprovechar la cache de dependencias
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
# Luego el codigo fuente y el empaquetado (sin ejecutar pruebas)
COPY src ./src
RUN mvn -q -B -DskipTests package

# ---- Etapa 2: imagen liviana solo con Java para ejecutar ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render asigna el puerto en la variable PORT (por defecto 8080)
ENV PORT=8080
# Perfil demo: base H2 con datos de ejemplo (Render no tiene MySQL).
# Para usar una base MySQL real, en Render se cambia a SPRING_PROFILES_ACTIVE=default
# y se definen SGFP_DB_URL, SGFP_DB_USER y SGFP_DB_PASSWORD.
ENV SPRING_PROFILES_ACTIVE=demo
EXPOSE 8080

# Datos de la base MySQL: se configuran en Render como variables de entorno
#   SGFP_DB_URL, SGFP_DB_USER, SGFP_DB_PASSWORD
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
