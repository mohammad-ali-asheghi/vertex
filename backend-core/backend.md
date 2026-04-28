# Backend Core Library

## 📖 Overview

**Backend Core** is a shared library for microservices architecture, providing common utilities, configurations, and
base classes for all backend services in the Vertex ecosystem.

## 📋 Table of Contents

- [Technical Stack](#-technical-stack)
- [Features](#-features)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Usage Examples](#-usage-examples)
- [Nexus Repository](#-nexus-repository)
- [Versioning](#-versioning)
- [Building from Source](#-building-from-source)
- [Dependencies](#-dependencies)
- [Contributing](#-contributing)

## 🛠 Technical Stack

| Technology   | Version     | Purpose               |
|--------------|-------------|-----------------------|
| Java         | 21          | Runtime & Development |
| Spring Boot  | 4.x         | Framework             |
| Spring Cloud | 2025.1.0    | Microservices         |
| JJWT         | 0.12.6      | JWT Authentication    |
| Redis OM     | 0.8.9       | Redis Object Mapping  |
| MapStruct    | 1.5.5.Final | DTO Mapping           |
| OpenAPI      | 2.7.0       | API Documentation     |

## ✨ Features

- 🔐 **JWT Utilities** - Token parsing, validation, and user extraction
- 💾 **Dynamic Query Builder** - Type-safe JPA criteria queries
- 📦 **Transaction Management** - Spring 7.0+ compatible wrapper
- 🏗 **Base Classes** - Entity, Repository, Service, and DTO Mapper interfaces
- 🔄 **Feign Clients** - Pre-configured HTTP clients
- 🛡 **Security Integration** - Spring Security ACL support
- 📊 **Actuator** - Production-ready monitoring endpoints
- 🔌 **Service Discovery** - Netflix Eureka client integration
- ⚡ **Circuit Breaker** - Resilience4j integration
- 📝 **API Documentation** - OpenAPI/Swagger UI support
- 🍃 **User Agent Parser** - YAUAA for device detection

## 📦 Installation

### Add to your project

#### Gradle (Kotlin DSL)

```kotlin
dependencies {
    api("com.vertex:backend-core:1.0.0")
}
```

#### Gradle (Groovy DSL)

```gradle
dependencies {
    api 'com.vertex:backend-core:1.0.0'
}
```

#### Maven

```xml

<dependency>
    <groupId>com.vertex</groupId>
    <artifactId>backend-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## ⚙ Configuration

### Required Application Properties

```yaml
# JWT Configuration
jwt:
  secret: ${JWT_SECRET:your-secret-key-min-32-characters}
  expiration: ${JWT_EXPIRATION:86400000}

# Service Discovery
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URI:http://localhost:8761/eureka}
  instance:
    prefer-ip-address: true

# Config Server
spring:
  config:
    import: optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}

  # Redis Configuration (for Redis OM)
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}

  # JPA Configuration
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

# Circuit Breaker
resilience4j:
  circuitbreaker:
    instances:
      backend-service:
        sliding-window-size: 10
        failure-rate-threshold: 50

# Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

## 🚀 Usage Examples

### JWT Token Handling

```java

@Autowired
private JwtUtils jwtUtils;

// Extract username from token
String username = JwtUtils.getCurrentUsername(token, secretKey);

// Validate token
boolean isValid = JwtUtils.validateToken(token, secretKey);
```

### Transaction Management

### Dynamic Queries

```java

@Autowired
private CriteriaQueryBuilder<User> queryBuilder;

// Build dynamic query with selected fields
List<String> fields = Arrays.asList("id", "username", "email");
CriteriaQuery<Object[]> query = queryBuilder.buildDynamicSelectionCriteria(
        User.class,
        restriction,
        fields
);
```

### Base Entity Extension

```java

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String username;
    private String email;
    // Your fields and methods
}
```

### DTO Mapping with MapStruct

```java

@Mapper
public interface UserMapper extends DTOMapper<User, UserDTO> {
    // Inherits all mapping methods
}

// Usage
UserDTO userDTO = userMapper.toDto(user);
User user = userMapper.toEntity(userDTO);
```

## 📦 Nexus Repository

This library is published to **Vertex Nexus Repository**.

### Repository Information

| Property     | Value                                              |
|--------------|----------------------------------------------------|
| **Name**     | Nexus Repository Manager                           |
| **URL**      | `http://localhost:7900/repository/maven-releases/` |
| **Type**     | Maven Releases                                     |
| **Protocol** | HTTP (Insecure - Development Only)                 |

### Access Credentials

Configure credentials in `~/.gradle/gradle.properties`:

```properties
nexus_username=admin
nexus_password=your-password
```

Or use environment variables:

```bash
export nexus_username=admin
export nexus_password=your-password
```

### Publishing to Nexus

```bash
# Publish to Nexus repository
./gradlew publish

# Publish with specific version
./gradlew publish -Pversion=1.0.1
```

### Accessing from Other Projects

#### Gradle Configuration

```gradle
repositories {
    maven {
        name = "nexus"
        url = uri("http://localhost:7900/repository/maven-releases/")
        allowInsecureProtocol = true
        credentials {
            username = project.findProperty("nexus_username") ?: "admin"
            password = project.findProperty("nexus_password") ?: "admin"
        }
    }
}
```

## 🔢 Versioning

We follow [Semantic Versioning](https://semver.org/):

- **Major** (1.x.x) - Incompatible API changes
- **Minor** (x.1.x) - Backward-compatible new features
- **Patch** (x.x.1) - Backward-compatible bug fixes

### Current Version: `1.0.0`

### Version History

| Version | Release Date | Changes                             |
|---------|--------------|-------------------------------------|
| 1.0.0   | 2026-04-28   | Initial release with core utilities |

## 🏗 Building from Source

### Prerequisites

- Java 21+
- Gradle 8.x+
- Access to Nexus repository

### Build Commands

```bash
# Clone repository
git clone <repository-url>
cd backend-core

# Build the library
./gradlew clean build

# Skip tests
./gradlew clean build -x test

# Generate coverage report
./gradlew test jacocoTestReport

# Publish to local Maven cache
./gradlew publishToMavenLocal

# Publish to Nexus
./gradlew publish
```

### Build Artifacts

After successful build, artifacts are located in:

```
build/libs/
├── backend-core-1.0.0.jar
└── backend-core-1.0.0-sources.jar
```

## 📚 Dependencies

### Core Dependencies

| Dependency                   | Version | Purpose                      |
|------------------------------|---------|------------------------------|
| Spring Boot Starter Web      | 3.x     | REST APIs                    |
| Spring Boot Starter Data JPA | 3.x     | Database access              |
| Spring Boot Starter Security | 3.x     | Authentication/Authorization |
| Spring Boot Starter Actuator | 3.x     | Monitoring                   |
| Spring Boot Starter AOP      | 3.x     | Aspect-oriented programming  |
| Spring Cloud Config Client   | Latest  | External configuration       |
| Spring Cloud Netflix Eureka  | Latest  | Service discovery            |
| Spring Cloud OpenFeign       | Latest  | Declarative REST clients     |
| Spring Cloud Circuit Breaker | Latest  | Resilience4j integration     |
| Spring Security ACL          | Latest  | Access control lists         |

### Utilities

| Dependency      | Version | Purpose                    |
|-----------------|---------|----------------------------|
| JJWT API        | 0.12.6  | JWT parsing and validation |
| Redis OM Spring | Latest  | Redis object mapping       |
| MapStruct       | Latest  | DTO mapping                |
| Lombok          | Latest  | Boilerplate reduction      |
| YAUAA           | Latest  | User agent parsing         |
| OpenAPI UI      | Latest  | API documentation          |

## 📁 Project Structure

```
backend-core/
├── src/main/java/com/vertex/backend/
│   ├── common/          # Base classes (Entity, Repository, Service)
│   ├── utils/           # Utilities (JWT, Query Builder)
│   ├── transaction/     # Transaction management
│   └── config/          # Auto-configurations
├── src/main/resources/
│   └── META-INF/
│       └── spring.factories  # Auto-configuration registration
└── build.gradle         # Build configuration
```

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests JwtUtilsTest

# Run tests with coverage
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

## 📄 License

This library is proprietary and confidential to Vertex.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Commit Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation
- `refactor:` Code refactoring
- `test:` Testing
- `chore:` Maintenance

## 🔗 Related Projects

- **core** - Base core library (`com.vertex:core`)
- **backend-services** - Microservices using this library
- **api-gateway** - Spring Cloud Gateway

---

**Maintained by Vertex Platform Team** | **Last Updated: 2026-04-28**