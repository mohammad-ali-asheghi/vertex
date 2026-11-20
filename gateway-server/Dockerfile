FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY build/libs/gateway-server-1.0.0.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
