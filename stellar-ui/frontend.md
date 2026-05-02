# 🚀 Core Front - Shared UI Library

This module (`core-front`) serves as the foundational shared frontend library (Shared JAR) for all microservices within
the platform. The primary goal is to enforce the DRY (Don't Repeat Yourself) principle, maintain a highly consistent
UI/UX across all applications, and centralize the management of base utilities in a micro-frontend architecture.

## 🏗 Architecture & Overview

Built on top of **Vaadin 25**, this module leverages the modern, official **Aura** theme. By simply adding this module
as a dependency, other microservices can inherit the core dashboard, security configurations, and essential utilities,
allowing developers to focus solely on their specific business logic.

### ✨ Key Features

* **Unified Dashboard (`MainDashboardLayout`):** Implemented using Vaadin's responsive `AppLayout`, featuring a Header (
  search bar, user profile, notifications), a Drawer (responsive sidebar), and a dynamic Content Area.
* **Theming & RTL Support:**
* Fully utilizes Aura theme CSS variables.
* Native Right-to-Left (RTL) support (`Direction.RIGHT_TO_LEFT`) for Persian localization.
* Integrated **Vazirmatn** typography globally via the `--lumo-font-family` CSS variable.
* **Smart Hierarchical Navigation:** Utilizes the modern `SideNav` component to recursively render parent-child menus
  with an elegant accordion-style behavior, moving away from legacy drill-down menus.
* **Localization (I18N):** A custom `CustomI18NProvider` hooked into Spring's `MessageSource` for seamless,
  high-performance translation management, optimized with Java 21's `Locale.of`.

## 🛠 Core Utilities

To streamline development across various microservices, the following utility classes are provided as singletons/static
helpers:

### 1. `AuthManager`

Handles user session management efficiently. It stores user access lists and hierarchical menus directly as Java objects
within the `VaadinSession`, eliminating the overhead of redundant JSON serialization/deserialization.

### 2. `Requester`

The robust engine for backend communication! This utility manages API calls and provides:

* Automatic handling of common HTTP OR HTTP/Feign errors (401 Unauthorized, 403 Forbidden, 500 Internal Server Error).
* Safe and thread-secure access to the Vaadin `UI` during background thread executions using
  `Optional.ofNullable(UI.getCurrent())`.

### 3. `Notifier`

A standardized notification wrapper using Vaadin's `Notification` component. It provides unified UI feedback (Success,
Error, Loading) across the platform. (e.g., system errors are automatically displayed for a consistent 5000ms duration).

### 4. `FeignConfig`

A smart `RequestInterceptor` that automatically extracts and injects the authentication token (Bearer Token) into the
headers of outgoing Feign client requests, ensuring secure inter-service communication.

## 💻 Usage in Microservices

1. Add the dependency to the target microservice's `pom.xml` or `build.gradle`:

```xml

<dependency>
    <groupId>com.yourcompany</groupId>
    <artifactId>core-front</artifactId>
    <version>1.0.0</version>
</dependency>
```

1. Inherit the shared layout in your new views:
   java
   @Route(value = "users", layout = MainDashboardLayout.Class)
   @PageTitle("User Management")
   public class UsersView extends VerticalLayout {
   // Implement your microservice-specific grids and forms here...
   }

## 🔮 Roadmap

*   [ ] **Keycloak Integration:** Centralized authentication and Single Sign-On (SSO) architecture.
*   [ ] **Dark/Light Mode Toggle:** User-selectable themes with OS-level synchronization.
*   [ ] **Redis Caching:** Implement caching for heavy datasets to optimize backend load and frontend rendering.

---
*Developed with ❤️, coffee, and Vaadin magic.*
