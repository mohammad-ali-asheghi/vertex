# 📚 Library Management Project

## 🧾 Project Overview

This project is a **Library Management System** fully implemented using **Java** and **Spring Boot** on the backend. The
main goal is to provide a powerful and extensible API for managing library resources such as books, users, roles, and
permissions.

On the frontend side, **Vaadin** is used. It consumes the backend APIs directly and allows extending their functionality
seamlessly.

---

## ⚙️ Technologies

* Java
* Spring Boot
* RESTful API
* Vaadin (Frontend)
* Maven / Gradle (depending on the setup)

---

## 🏗️ Project Architecture

The project follows a modular structure and includes the following components:

* **API Layer**: Controllers responsible for handling requests (e.g., `ApplicationApi`, `UserInfoApi`, `RoleApi`, etc.)
* **DTOs**: Data Transfer Objects for communication between layers
* **Enums**: Constant values such as response codes and permission types
* **Exceptions**: Error handling (e.g., `ServiceException`, `BindingException`)
* **Mapper**: Responsible for converting between entities and DTOs
* **Utils**: General-purpose utility classes

---

## 🔐 Authentication & Authorization

The project includes an **OAuth module** for handling authentication and authorization. Roles and permissions are
dynamically manageable.

---

## 🔌 API Usage

All APIs are designed in a RESTful manner and can be consumed by any client, including the Vaadin frontend or other
external applications.

Example endpoints:

```
/api/users
/api/roles
/api/permissions
```

---

## 🎨 Frontend (Vaadin)

The frontend is built using **Vaadin**, which:

* Connects directly to backend APIs
* Supports extension and customization of components
* Provides a seamless Java-based UI development experience

---

## 🚀 Project Goals

* Build a scalable and extensible library management system
* Maintain clear separation between backend and frontend
* Provide standardized APIs for integration with other systems

---

## 📌 Notes

* The project structure is designed for easy scalability and maintainability
* New modules (e.g., lending management, reporting, etc.) can be added بسهولة
* Focus has been placed on clean architecture and long-term maintainability

---

## 👨‍💻 Author

This project is developed as a professional example of building enterprise-grade systems using Spring Boot and Vaadin.
