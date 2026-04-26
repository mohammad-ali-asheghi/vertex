# Config Server

The Config Server is a Spring Boot service that centralizes external configuration for Vertex services. It runs as a
Spring Cloud Config Server, reads configuration from a Git-backed repository, and registers itself with Eureka for
service discovery.

## Responsibilities

- Exposes centralized configuration through Spring Cloud Config Server endpoints.
- Loads configuration from a remote Git repository using encrypted connection values.
- Clones the configuration repository on startup and force-pulls updates to stay synchronized.
- Registers with Eureka so other services can discover the config server.
- Exposes actuator endpoints for health checks and refresh operations.

## Runtime Configuration

- Application name: `CONFIG-SERVER`
- Server port: `8888`
- Eureka default zone: `http://localhost:7801/eureka`
- Git default label: `main`
- Git repository values and search paths are stored with Jasypt encrypted values.
- Exposed actuator endpoints: `health`, `refresh`

## Main Components

- `ConfigServerApplication` starts the Spring Boot application.
- `@EnableConfigServer` enables Spring Cloud Config Server behavior.
- `@EnableDiscoveryClient` enables Eureka discovery registration.
- `application.yaml` defines the Git backend, server port, Eureka client, and actuator exposure.

## Dependencies

- Spring Boot web starter for HTTP support.
- Spring Boot actuator for operational endpoints.
- Spring Cloud Config Server for centralized configuration serving.
- Spring Cloud Netflix Eureka Client for service discovery.
- Jasypt Spring Boot starter for encrypted configuration values.
