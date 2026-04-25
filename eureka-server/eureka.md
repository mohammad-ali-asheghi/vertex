# Eureka Server Notes

## What changed

This service is now being converted from a plain Spring Boot application into a
Netflix Eureka service registry.

Updated files:

- `build.gradle`
  - adds `spring-cloud-starter-netflix-eureka-server`
  - adds `jasypt-spring-boot-starter`
  - imports the Spring Cloud BOM with version `2025.1.0`
- `src/main/java/com/vertex/eureka/EurekaServerApplication.java`
  - enables the Eureka server with `@EnableEurekaServer`
- `src/main/resources/application.yaml`
  - renames the app to `EUREKA-SERVER`
  - binds the server to port `7801`
  - disables self-registration and registry fetch
  - uses an encrypted `defaultZone` value

## Current runtime behavior

With the current configuration, this application acts as a standalone Eureka
server:

- it starts on `http://localhost:7801`
- it does not try to register itself into another registry
- it does not fetch registry data from another registry
- it can accept registrations from client services

This is the right baseline for a single-node discovery server.

## Configuration details

Main config:

```yaml
spring:
  application:
    name: EUREKA-SERVER
server:
  port: 7801
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: ENC(...)
```

Important note about `defaultZone`:

- for a standalone Eureka server, `defaultZone` is usually not required
- it becomes useful when you run multiple Eureka nodes and want peer
  replication
- because the value is encrypted, the app needs a Jasypt password at runtime

## Required environment variable

If you keep the encrypted `defaultZone`, start the app with a Jasypt password.
Example:

```bash
export JASYPT_ENCRYPTOR_PASSWORD='your-secret'
./gradlew bootRun
```

Without that password, Spring may fail while binding encrypted properties.

## Local access

After startup, open:

- app: `http://localhost:7801`
- Eureka dashboard: `http://localhost:7801/`

## How a client service should register

Example client-side configuration:

```yaml
spring:
  application:
    name: ORDER-SERVICE
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7801/eureka/
```

## Recommendations

### Keep as-is when

Keep the current setup if:

- this is meant to be a single Eureka node for local/dev usage
- you already manage the Jasypt password through environment variables or a
  secrets manager

### Simplify when

You can simplify the config by removing `eureka.client.service-url.defaultZone`
if this server will not replicate to another Eureka node.

That would remove the need to decrypt this property on the server side.

### Extend when

If you want this for production, consider adding:

- Spring Boot Actuator for health and readiness endpoints
- profile-based configs such as `application-dev.yaml` and
  `application-prod.yaml`
- multiple Eureka peers for high availability
- explicit security in front of the dashboard and registry endpoints

## Implemented follow-up

I added `src/test/resources/application.yaml` so tests can boot with a local
plain-text registry URL instead of the encrypted production-style value.

Test override:

```yaml
spring:
  application:
    name: EUREKA-SERVER-TEST
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7801/eureka/
```

## Next step

If you want, the next clean improvement is to either:

1. remove the encrypted `defaultZone` from the server config for single-node use
2. keep it and add profile-specific peer replication config for clustered use
