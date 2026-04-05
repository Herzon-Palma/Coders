FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copiamos el wrapper de maven y el pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargamos dependencias
RUN ./mvnw dependency:go-offline

# Copiamos el código fuente
COPY src src

# Compilamos y empaquetamos
RUN ./mvnw package -DskipTests

# Exponemos el puerto
EXPOSE 8080

# Comando para ejecutar (buscamos el jar en target)
ENTRYPOINT ["sh", "-c", "java -jar target/*.jar"]
