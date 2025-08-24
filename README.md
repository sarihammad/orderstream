# OrderStream

An e-commerce order management system built with Spring Boot and React. Features atomic order placement, real-time inventory management, role-based access control, and asynchronous invoice generation with AWS Lambda integration.

---

## Architecture

```mermaid
graph TD
    %% Frontend Layer
    subgraph "Frontend Layer (React)"
        A1["User Browser"] -->|"HTTP/HTTPS"| A2["React Admin Dashboard"]
        A2 -->|"API Calls"| A3["Authentication<br>(JWT Token)"]
        A2 -->|"Product Management"| A4["Product CRUD<br>Operations"]
        A2 -->|"Order Management"| A5["Order Processing<br>& Status"]
    end

    %% Backend Layer
    subgraph "Backend Layer (Spring Boot)"
        B1["REST Controllers<br>(Auth, Product, Order)"] -->|"Business Logic"| B2["Service Layer<br>(Auth, Product, Order)"]
        B2 -->|"Data Access"| B3["Repository Layer<br>(JPA Repositories)"]
        B2 -->|"Security"| B4["Security Config<br>(JWT, Roles)"]
        B2 -->|"Async Processing"| B5["Queue Service<br>(Redis)"]
        B2 -->|"AWS Integration"| B6["Lambda Service<br>(Invoice Gen)"]
    end

    %% Data Layer
    subgraph "Data Layer"
        C1["PostgreSQL<br>(Orders, Products, Users)"] -->|"Primary Data"| C2["JPA Entities<br>(Order, Product, User)"]
        C3["Redis<br>(Cache & Queue)"] -->|"Caching"| C4["Product Cache"]
        C3 -->|"Job Queue"| C5["Invoice Queue"]
    end

    %% Infrastructure Layer
    subgraph "Infrastructure Layer"
        D1["AWS Lambda<br>(Invoice Generation)"] -->|"Async Processing"| D2["S3 Storage<br>(Invoice PDFs)"]
        D3["Docker Containers"] -->|"Containerization"| D4["Microservices<br>Deployment"]
    end

    %% Cross-layer connections
    A2 -->|"API Requests"| B1
    B3 -->|"Database Operations"| C1
    B5 -->|"Queue Jobs"| C3
    B6 -->|"Trigger Lambda"| D1
    C3 -->|"Cache Hits"| B2
```

---

## System Overview

OrderStream implements a layered architecture with clear separation of concerns across four main layers:

### **Frontend Layer**

- **React Admin Dashboard**: Modern UI for order and product management
- **Authentication**: JWT-based login with role-based access control
- **Real-time Updates**: Live order status and inventory tracking
- **Responsive Design**: Mobile-friendly interface with Tailwind CSS

### **Backend Layer**

- **REST Controllers**: Handle HTTP requests for authentication, products, and orders
- **Service Layer**: Business logic for order processing, inventory management, and user authentication
- **Security**: JWT authentication with role-based authorization (USER/ADMIN)
- **Async Processing**: Redis-based job queue for invoice generation

### **Data Layer**

- **PostgreSQL**: Primary database for orders, products, and users with row-level locking
- **Redis**: Caching layer for product data and job queue for async operations
- **JPA Entities**: Domain models with proper relationships and constraints

### **Infrastructure Layer**

- **AWS Lambda**: Serverless invoice generation triggered by order completion
- **Docker**: Containerized deployment with Docker Compose
- **S3**: Cloud storage for generated invoice PDFs

The system supports three user roles:

1. **Anonymous Users**: Can browse products and register/login
2. **Authenticated Users**: Can place orders and view their order history
3. **Admin Users**: Can manage products, view all orders, and access admin dashboard

---

## Data Flow

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend
    participant B as Backend
    participant DB as PostgreSQL
    participant R as Redis
    participant L as AWS Lambda
    participant S3 as S3

    %% User Authentication
    U->>F: Login with credentials
    F->>B: POST /api/auth/login
    B->>DB: Validate user credentials
    DB-->>B: User data
    B-->>F: JWT token
    F-->>U: Store token & redirect

    %% Product Browsing
    U->>F: Browse products
    F->>B: GET /api/products
    B->>R: Check cache
    alt Cache Hit
        R-->>B: Cached products
    else Cache Miss
        B->>DB: Query products
        DB-->>B: Product data
        B->>R: Cache products
    end
    B-->>F: Product list
    F-->>U: Display products

    %% Order Placement
    U->>F: Place order
    F->>B: POST /api/orders
    B->>DB: Begin transaction
    B->>DB: Lock products (row-level)
    B->>DB: Check stock availability
    B->>DB: Decrease stock
    B->>DB: Create order
    B->>DB: Commit transaction
    B->>R: Publish to invoice queue
    B-->>F: Order confirmation
    F-->>U: Order success

    %% Async Invoice Generation
    R->>L: Process invoice job
    L->>DB: Fetch order details
    L->>L: Generate PDF invoice
    L->>S3: Upload invoice
    L->>DB: Update order with invoice URL
```

---

## Features

- **Atomic Order Processing**: Database transactions with row-level locking prevent race conditions
- **Real-time Inventory**: Stock updates with optimistic locking for high concurrency
- **Role-based Access**: JWT authentication with USER/ADMIN roles
- **Product Management**: CRUD operations with stock tracking and low-stock alerts
- **Order Management**: Complete order lifecycle with status tracking
- **Async Invoice Generation**: AWS Lambda integration for PDF generation
- **Caching**: Redis-based product caching for improved performance
- **Admin Dashboard**: React-based interface for order and product management
- **Docker Support**: Complete containerized deployment with health checks
- **Monitoring**: Comprehensive health checks and metrics

---

## Tech Stack

- **Frontend**: React, TypeScript, Tailwind CSS, Axios
- **Backend**: Spring Boot 3.5.3, Java 17, Spring Security, JWT
- **Database**: PostgreSQL 15 with row-level locking
- **Cache/Queue**: Redis 7 for caching and job queues
- **Cloud**: AWS Lambda, S3, SNS
- **Container**: Docker, Docker Compose
- **Build**: Maven, npm
- **Testing**: JUnit 5, Testcontainers
