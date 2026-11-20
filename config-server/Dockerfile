FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY build/libs/config-server-1.0.0.jar app.jar
COPY src/main/resources/config /app/config
EXPOSE 8888
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
