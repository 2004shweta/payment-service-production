# 💳 Payment Service

A production-style backend payment service built with **Java 17, Spring Boot, PostgreSQL, Redis, RabbitMQ, Docker, and AWS**.

The project demonstrates backend API development, database persistence, caching, asynchronous messaging, idempotency, containerization, CI/CD, and cloud deployment.

---

## 🏗️ Architecture

```text
                         ┌──────────────────┐
                         │      GitHub      │
                         │  Source Control  │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │ GitHub Actions   │
                         │   CI / CD        │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │    Amazon ECR    │
                         │ Docker Registry  │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │     AWS EC2      │
                         │    Ubuntu 24.04  │
                         └────────┬─────────┘
                                  │
                         Docker Compose
                                  │
              ┌───────────────────┼───────────────────┐
              │                   │                   │
              ▼                   ▼                   ▼
       ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
       │ Spring Boot │     │ PostgreSQL  │     │    Redis    │
       │ Application │     │  Database   │     │    Cache    │
       └──────┬──────┘     └─────────────┘     └─────────────┘
              │
              ▼
       ┌─────────────┐
       │  RabbitMQ   │
       │  Messaging  │
       └─────────────┘
```

---

## ✨ Key Features

- RESTful payment APIs
- Payment creation, processing, retry, cancellation, and retrieval
- Idempotency support for payment requests
- Payment audit records
- PostgreSQL persistence using Spring Data JPA
- Redis integration for caching
- RabbitMQ integration for asynchronous messaging
- Request validation
- Spring Boot Actuator health monitoring
- Dockerized application
- Multi-stage Docker build
- Non-root Docker runtime
- Docker health checks
- Automated CI pipeline
- Automated AWS deployment
- Amazon ECR image storage
- Self-hosted GitHub Actions runner on EC2

---

## 🛠️ Technology Stack

### Backend

| Technology | Purpose |
|---|---|
| Java 17 | Application development |
| Spring Boot | Backend framework |
| Spring Web | REST APIs |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| Spring Validation | Request validation |
| Spring Boot Actuator | Health monitoring |

### Database & Messaging

| Technology | Purpose |
|---|---|
| PostgreSQL | Relational data persistence |
| Redis | In-memory caching |
| RabbitMQ | Asynchronous messaging |

### DevOps & Cloud

| Technology | Purpose |
|---|---|
| Docker | Containerization |
| Docker Compose | Multi-container orchestration |
| GitHub Actions | CI/CD automation |
| Amazon ECR | Container image registry |
| AWS EC2 | Application hosting |
| AWS IAM | AWS permissions |
| AWS EBS | EC2 storage |
| Ubuntu 24.04 | Server operating system |

### Testing & API Documentation

| Technology | Purpose |
|---|---|
| JUnit | Unit testing |
| Spring Boot Test | Application testing |
| Testcontainers | Integration testing with containers |
| Swagger / OpenAPI | API documentation |
| Postman | API testing |

---

# 📁 Project Structure

```text
payment-service/
│
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── deploy.yml
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-prod.yml
│   │
│   └── test/
│
├── .dockerignore
├── Dockerfile
├── docker-compose.yml
├── docker-compose.aws.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
├── LICENSE
└── README.md
```

---

# 🚀 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/payments` | Create a payment |
| `GET` | `/api/v1/payments/{id}` | Retrieve a payment |
| `POST` | `/api/v1/payments/{id}/process` | Process a payment |
| `POST` | `/api/v1/payments/{id}/retry` | Retry a payment |
| `POST` | `/api/v1/payments/{id}/cancel` | Cancel a payment |
| `GET` | `/api/v1/health` | Application health |

---

# 🔑 Idempotency

The payment creation flow supports an **idempotency key** to help prevent duplicate payment creation when the same request is retried.

Example request:

```json
{
  "idempotencyKey": "payment-12345",
  "merchantId": "merchant-001",
  "customerId": "customer-001",
  "amount": 100.00,
  "currency": "USD",
  "description": "Payment for order",
  "callbackUrl": ""
}
```

The idempotency mechanism is particularly relevant for payment APIs where clients may retry requests because of network failures or timeouts.

---

# 🗄️ PostgreSQL

PostgreSQL is used as the primary relational database.

The application persists payment-related information including:

- Payment records
- Payment audit records
- Idempotency information

Persistence is implemented using **Spring Data JPA and Hibernate**.

---

# ⚡ Redis

Redis is integrated as an in-memory caching layer.

It is used to reduce repeated database access for frequently requested payment information.

The application communicates with Redis through the Docker Compose service network when running in the containerized environment.

---

# 📨 RabbitMQ

RabbitMQ provides asynchronous messaging capabilities within the payment service.

Using a message broker allows processing that does not need to block the main request flow to be handled asynchronously.

---

# 🧪 Testing

The project includes testing support using:

- JUnit
- Spring Boot Test
- Testcontainers

Run the test suite with:

### Linux / macOS

```bash
./mvnw clean test
```

### Windows

```cmd
mvnw.cmd clean test
```

---

# 🐳 Docker

The application uses a **multi-stage Docker build**.

The build stage compiles the Spring Boot application, while the runtime stage uses a smaller Java runtime image.

The application container runs using a dedicated non-root user:

```dockerfile
USER spring:spring
```

This reduces unnecessary privileges inside the container.

### Build the image

```bash
docker build -t payment-service:local .
```

### Start the complete local stack

```bash
docker compose up -d
```

### Check containers

```bash
docker compose ps
```

### View application logs

```bash
docker compose logs -f payment-service
```

### Stop services

```bash
docker compose down
```

---

# ☁️ AWS Deployment

The application is deployed to **Amazon EC2** using Docker Compose.

### AWS Components

| AWS Service | Purpose |
|---|---|
| EC2 | Application hosting |
| ECR | Docker image storage |
| IAM | AWS permissions |
| EBS | EC2 storage |
| Security Groups | Network access control |

The EC2 instance runs Ubuntu 24.04 LTS and hosts the application and supporting services through Docker Compose.

---

# 🔄 CI/CD Pipeline

Every push to the `main` branch triggers the CI/CD workflow.

## Continuous Integration

The CI workflow:

```text
Git Push
    │
    ▼
Checkout Source
    │
    ▼
Configure Java 17
    │
    ▼
Run Maven Tests
    │
    ▼
Build Application
```

## Continuous Deployment

The deployment workflow:

```text
Git Push
    │
    ▼
GitHub Actions
    │
    ▼
EC2 Self-hosted Runner
    │
    ▼
Authenticate with Amazon ECR
    │
    ▼
Pull Latest Docker Image
    │
    ▼
Docker Compose Deployment
    │
    ▼
Application Health Check
```

The deployment runner is configured as a system service on the EC2 instance, allowing the runner to remain available without keeping an SSH session open.

---

# ❤️ Health Monitoring

The application exposes a health endpoint:

```text
GET /api/v1/health
```

Example response:

```json
{
  "service": "payment-service",
  "status": "UP",
  "timestamp": "2026-10-05T14:28:05.994032844"
}
```

Spring Boot Actuator is also configured for application monitoring.

---

# 📚 Swagger / OpenAPI

Swagger UI is available locally at:

```text
http://localhost:8080/swagger-ui/index.html
```

Live deployment:

```text
http://3.107.156.100:8080/swagger-ui/index.html
```

The deployed health endpoint is:

```text
http://3.107.156.100:8080/api/v1/health
```

---

# ⚙️ Environment Configuration

Local and production configurations are maintained separately:

```text
application.yml
application-prod.yml
```

Production infrastructure connection details are supplied through environment variables.

This keeps environment-specific configuration separate from the application code.

---

# 🔐 Security Considerations

The Docker runtime uses a non-root user.

AWS access is provided through IAM permissions attached to the EC2 instance rather than storing long-lived AWS credentials on the server.

Production hardening that can be added in future versions includes:

- HTTPS with a domain
- AWS Secrets Manager
- Authentication and authorization
- Centralized logging
- Container vulnerability scanning
- Database backup strategy
- Infrastructure as Code

---

# 📸 Screenshots

### Swagger API Documentation

Add your Swagger screenshot here:

```text
docs/images/swagger.png
```

### AWS Deployment

Add your EC2 / deployment screenshot here:

```text
docs/images/aws-deployment.png
```

### CI/CD Pipeline

Add your successful GitHub Actions screenshot here:

```text
docs/images/github-actions.png
```

---

# 🔮 Future Improvements

Planned improvements include:

- HTTPS with a domain and load balancer
- Authentication and authorization
- AWS Secrets Manager integration
- Centralized logging
- Prometheus / Grafana monitoring
- Automated database backups
- Automated database migrations
- Blue/green or rolling deployments
- Container vulnerability scanning
- Terraform-based Infrastructure as Code

---

# 👩‍💻 Author

**Shweta Jaiswal**

B.Tech — Computer Science Engineering

---

⭐ If you found this project interesting, consider giving it a star.
