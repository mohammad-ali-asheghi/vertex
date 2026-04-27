# Vertex Gateway Server

Vertex Gateway Server is a reactive API Gateway built with **Spring Cloud Gateway**, designed to act as the entry point
for the Vertex microservices ecosystem.

It provides centralized routing, service discovery integration, and externalized configuration through Spring Cloud
Config.

---

# Tech Stack

- Java 21
- Spring Boot 4.0.6
- Spring Cloud 2025.1.0
- Spring Cloud Gateway (WebFlux)
- Spring Cloud Config Client
- Netflix Eureka Client
- Gradle
- Lombok

---

# Project Information

Group: `com.vertex`  
Artifact: `gateway-server`  
Version: `1.0.0`

The gateway is responsible for:

- Routing requests to internal services
- Integrating with service discovery (Eureka)
- Fetching centralized configuration from Config Server
- Providing operational metrics via Actuator
- Acting as the single entry point to the microservice architecture

---

# Java Configuration

The project uses **Java Toolchains** to enforce Java 21.

```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
