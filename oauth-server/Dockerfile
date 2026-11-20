FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY build/libs/oauth-server-1.0.0.jar app.jar
EXPOSE 7901
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
