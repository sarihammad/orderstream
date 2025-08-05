# OrderStream - Scalable Inventory & Order Management System

A comprehensive e-commerce backend system built with Spring Boot, featuring atomic order placement, stock management, role-based access control, and async invoice generation.

## 🚀 Features

### Core Features

- **🔐 Authentication & Authorization**: JWT-based authentication with role-based access (USER/ADMIN)
- **🛍️ Product Management**: CRUD operations for products with stock tracking
- **🧾 Order System**: Transactional order placement with concurrency control
- **📦 Inventory Management**: Real-time stock updates with row-level locking
- **🖥️ Admin Dashboard**: React-based admin interface for order management
- **📄 Async Invoice Generation**: AWS Lambda integration for invoice processing

### Technical Features

- **Concurrency Control**: PostgreSQL row-level locking for race condition prevention
- **Caching**: Redis-based caching for product data
- **Queue System**: Redis-based job queue for async processing
- **Cloud Integration**: AWS Lambda, S3, and SNS integration
- **Docker Support**: Complete containerized deployment
- **Health Checks**: Comprehensive monitoring and health endpoints

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend│    │  Spring Boot    │    │   PostgreSQL    │
│   (Admin UI)    │◄──►│   Backend       │◄──►│   Database      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │     Redis       │
                       │  (Cache/Queue)  │
                       └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   AWS Lambda    │
                       │ (Invoice Gen)   │
                       └─────────────────┘
```

## 🛠️ Tech Stack

| Layer           | Technology                 |
| --------------- | -------------------------- |
| **Backend**     | Spring Boot 3.5.3, Java 17 |
| **Database**    | PostgreSQL 15              |
| **Cache/Queue** | Redis 7                    |
| **Security**    | Spring Security, JWT       |
| **Cloud**       | AWS Lambda, S3, SNS        |
| **Container**   | Docker, Docker Compose     |
| **Testing**     | JUnit 5, Testcontainers    |

## 📁 Project Structure

```
orderstream/
├── backend/
│   ├── src/main/java/ca/devign/orderstream/
│   │   ├── config/           # Security, JWT, Redis, AWS
│   │   ├── controller/       # REST Controllers
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── entity/          # JPA Entities
│   │   ├── repository/      # Data Access Layer
│   │   ├── service/         # Business Logic
│   │   ├── queue/           # Redis Queue Management
│   │   └── lambda/          # AWS Lambda Integration
│   ├── src/main/resources/
│   │   └── application.yml  # Configuration
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                # React Admin Dashboard
├── docker-compose.yml
└── README.md
```

## 🚀 Quick Start

### Prerequisites

- Docker and Docker Compose
- Java 17+
- Maven 3.6+

### 1. Clone and Setup

```bash
git clone <repository-url>
cd orderstream
```

### 2. Start the System

```bash
docker-compose up -d
```

This will start:

- PostgreSQL database on port 5432
- Redis cache/queue on port 6379
- Spring Boot backend on port 8080

### 3. Verify Installation

```bash
# Check if services are running
docker-compose ps

# Test the API
curl http://localhost:8080/api/health
```

## 🔐 Authentication

### Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

## 📚 API Documentation

### Authentication Endpoints

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login

### Product Endpoints

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create product (ADMIN only)
- `PUT /api/products/{id}` - Update product (ADMIN only)
- `DELETE /api/products/{id}` - Delete product (ADMIN only)
- `GET /api/products/low-stock` - Get low stock products (ADMIN only)

### Order Endpoints

- `POST /api/orders` - Create new order
- `GET /api/orders/my` - Get user's orders
- `GET /api/orders` - Get all orders (ADMIN only)
- `GET /api/orders/{id}` - Get order by ID
- `PUT /api/orders/{id}/status` - Update order status (ADMIN only)

## 🔧 Configuration

### Environment Variables

```yaml
# Database
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/orderstream
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: postgres

# Redis
SPRING_DATA_REDIS_HOST: localhost
SPRING_DATA_REDIS_PORT: 6379

# JWT
JWT_SECRET: your-secret-key
JWT_EXPIRATION: 86400000

# AWS
AWS_REGION: us-east-1
AWS_ACCESS_KEY_ID: your-access-key
AWS_SECRET_ACCESS_KEY: your-secret-key
```

## 🧪 Testing

### Run Tests

```bash
cd backend
mvn test
```

### Integration Tests

```bash
# Start test containers
docker-compose -f docker-compose.test.yml up -d

# Run integration tests
mvn test -Dspring.profiles.active=test
```

## 🐳 Docker Deployment

### Development

```bash
docker-compose up -d
```

### Production

```bash
# Build and start production services
docker-compose -f docker-compose.prod.yml up -d
```

## 📊 Monitoring

### Health Checks

- Application: `GET /api/health`
- Database: `GET /actuator/health`
- Redis: `GET /actuator/health`

### Metrics

- Application metrics: `GET /actuator/metrics`
- Custom business metrics available

## 🔒 Security Features

### Authentication

- JWT-based stateless authentication
- Password encryption with BCrypt
- Token expiration and refresh

### Authorization

- Role-based access control (USER/ADMIN)
- Method-level security annotations
- Resource-level access control

### Data Protection

- Input validation and sanitization
- SQL injection prevention
- XSS protection

## 🚀 Performance Features

### Caching

- Redis-based product caching
- Cache eviction strategies
- Distributed caching support

### Concurrency

- PostgreSQL row-level locking
- Optimistic locking for high concurrency
- Deadlock prevention strategies

### Async Processing

- Redis-based job queues
- AWS Lambda integration
- Non-blocking operations

## 🔧 Development

### Local Development

```bash
# Start dependencies
docker-compose up postgres redis -d

# Run application
cd backend
mvn spring-boot:run
```

### Code Quality

```bash
# Run linting
mvn checkstyle:check

# Run tests
mvn test

# Generate coverage report
mvn jacoco:report
```

## 📈 Scalability

### Horizontal Scaling

- Stateless application design
- Database connection pooling
- Redis cluster support

### Performance Optimization

- Database indexing strategies
- Query optimization
- Caching strategies

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:

- Create an issue in the repository
- Check the documentation
- Review the test cases

---

**OrderStream** - Built with ❤️ using Spring Boot, Redis, and AWS
