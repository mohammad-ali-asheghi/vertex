FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY build/libs/eureka-server-1.0.0.jar app.jar
EXPOSE 7801
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
