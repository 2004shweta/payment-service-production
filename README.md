# 💳 Payment Service

<p align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7-red?style=for-the-badge&logo=redis)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3-orange?style=for-the-badge&logo=rabbitmq)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker)
![AWS](https://img.shields.io/badge/AWS-Cloud-FF9900?style=for-the-badge&logo=amazonaws)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-2088FF?style=for-the-badge&logo=githubactions)

</p>

<p align="center">
  A production-oriented backend payment service built with <b>Java, Spring Boot, PostgreSQL, Redis, RabbitMQ, Docker, and AWS</b>.
</p>

<p align="center">
  Demonstrates backend development, containerization, asynchronous messaging, caching, database persistence, health monitoring, and automated CI/CD deployment.
</p>

---

## 🏗️ Architecture

text
                         ┌──────────────────┐
                         │     GitHub       │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │ GitHub Actions   │
                         │    CI / CD       │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │    AWS ECR       │
                         │ Docker Registry  │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │     AWS EC2      │
                         │ Ubuntu 24.04 LTS │
                         └────────┬─────────┘
                                  │
                           Docker Compose
                                  │
              ┌───────────────────┼───────────────────┐
              │                   │                   │
              ▼                   ▼                   ▼
       ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
       │ Spring Boot │     │ PostgreSQL  │     │    Redis    │
       │ Application │     │  Database   │     │   Cache     │
       └──────┬──────┘     └─────────────┘     └─────────────┘
              │
              ▼
       ┌─────────────┐
       │  RabbitMQ   │
       │  Messaging  │
       └─────────────┘

## 🛠️ Tech Stack

### ☕ Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Boot Actuator

### 🗄️ Database & Messaging

- PostgreSQL
- Redis
- RabbitMQ
- Hibernate

### ☁️ DevOps & Cloud

- Docker
- Docker Compose
- GitHub Actions
- AWS EC2
- Amazon ECR
- AWS IAM
- AWS EBS
- Ubuntu 24.04

### 🧪 Testing & API

- JUnit
- Spring Boot Test
- Testcontainers
- Postman
- Swagger / OpenAPI

---

## ✨ Key Features

- 🔹 RESTful payment APIs
- 🔹 PostgreSQL persistence using Spring Data JPA
- 🔹 Redis integration for caching
- 🔹 RabbitMQ integration for asynchronous messaging
- 🔹 Idempotency support for payment requests
- 🔹 Payment audit records
- 🔹 Input validation
- 🔹 Production-specific Spring configuration
- 🔹 Dockerized application
- 🔹 Multi-stage Docker build
- 🔹 Non-root Docker container
- 🔹 Spring Boot Actuator health monitoring
- 🔹 Docker health checks
- 🔹 Automated CI pipeline
- 🔹 Automated AWS deployment
- 🔹 Amazon ECR container image storage
- 🔹 Self-hosted GitHub Actions runner on EC2

---

## 📁 Project Structure


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
├── Dockerfile
├── docker-compose.yml
├── docker-compose.aws.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md


# 🚀 Running Locally

## 📋 Prerequisites

Make sure you have the following installed:

- Java 17
- Maven
- Docker Desktop
- Git

---

## 📥 Clone the Repository


git clone https://github.com/2004shweta/payment-service-production.git
cd payment-service-production


---

## 🐳 Start the Application

Start all services using Docker Compose:


docker compose up -d


Check running containers:


docker compose ps


---

## ❤️ Health Check

Check whether the application is running:

```bash
curl http://localhost:8080/api/v1/health
```

Expected response:

json
{
  "service": "payment-service",
  "status": "UP"
}


---

# ☕ Running with Maven

### Run Tests

Linux/macOS:

```bash
./mvnw clean test
```

Windows:

```cmd
mvnw.cmd clean test
```

### Build the Application

```bash
./mvnw package -DskipTests
```

Windows:

```cmd
mvnw.cmd package -DskipTests
```

---

# 🐳 Docker

### Build the Application Image

```bash
docker build -t payment-service:local .
```

### Run Using Docker Compose

```bash
docker compose up -d
```

### View Application Logs

```bash
docker compose logs -f payment-service
```

### Stop Services

```bash
docker compose down
```

---

# ☁️ AWS Deployment

The application is deployed to **AWS EC2** using Docker Compose.

## AWS Components

| Service | Purpose |
|---|---|
| 🖥️ EC2 | Application hosting |
| 📦 ECR | Docker image storage |
| 🔐 IAM | AWS permissions |
| 🛡️ Security Groups | Network access control |
| 💾 EBS | EC2 storage |
| 🐳 Docker Compose | Container orchestration |

The EC2 instance runs **Ubuntu 24.04 LTS** and hosts the application containers.

---

# 🔄 CI/CD Pipeline

Every push to the `main` branch triggers the GitHub Actions pipeline.

## 🧪 Continuous Integration

The CI pipeline performs:

1. 📥 Checkout source code
2. ☕ Configure Java 17
3. 🧪 Run Maven tests
4. 📦 Build the application


Git Push
   │
   ▼
Checkout Source
   │
   ▼
Java 17
   │
   ▼
Maven Tests
   │
   ▼
Maven Build


---

## 🚀 Continuous Deployment

The deployment pipeline performs:

1. 🖥️ Runs on the EC2 self-hosted GitHub Actions runner
2. 🔐 Authenticates with Amazon ECR
3. 📥 Pulls the latest Docker image
4. 🐳 Updates Docker Compose services
5. ❤️ Performs an application health check


Git Push
   │
   ▼
GitHub Actions
   │
   ▼
EC2 Self-hosted Runner
   │
   ▼
Amazon ECR
   │
   ▼
Docker Compose
   │
   ▼
Health Check


---

# ❤️ Health Monitoring

Application health endpoint:


GET /api/v1/health


Example:

json
{
  "service": "payment-service",
  "status": "UP",
  "timestamp": "2026-10-05T14:28:05.994032844"
}


Spring Boot Actuator is also configured for application monitoring.

---

# 📚 API Documentation

Swagger / OpenAPI documentation is available at:


http://localhost:8080/swagger-ui/index.html


For the deployed EC2 instance:


http://<EC2-PUBLIC-IP>:8080/swagger-ui/index.html


---

# 🗄️ Database

PostgreSQL is used as the primary relational database.

The application maintains payment-related data including:

- 💳 Payments
- 📝 Payment audit records
- 🔑 Idempotency keys

Persistence is handled using **Spring Data JPA and Hibernate**.

---

# ⚡ Redis

Redis is used for:

- Fast in-memory data access
- Caching
- Reducing repeated database access

---

# 📨 RabbitMQ

RabbitMQ provides asynchronous messaging between application components.

This allows operations that do not need to block the main request flow to be handled asynchronously.

---

# 🔐 Docker Security

The application Docker image uses a **multi-stage build** to separate the build environment from the runtime environment.

The runtime container runs as a non-root user:

```dockerfile
USER spring:spring
```

This reduces the privileges available to the application container.

---

# ⚙️ Environment Configuration

Local and production configurations are maintained separately:


application.yml
application-prod.yml


Production infrastructure connection details are supplied through environment variables rather than being hardcoded into the application.

---

# 🔧 Useful Commands

### Check Docker Containers

```bash
docker ps
```

### Check Docker Compose Services

```bash
docker compose ps
```

### View Application Logs

```bash
docker logs -f payment-service-app
```

### Check Application Health

```bash
curl http://localhost:8080/api/v1/health
```

### Check GitHub Actions Runner

```bash
sudo ./svc.sh status
```

### Restart GitHub Actions Runner

```bash
sudo ./svc.sh restart
```

---

# ✅ CI/CD Result

The project supports:

- ✅ Automated testing
- ✅ Automated Maven builds
- ✅ Docker-based deployment
- ✅ Amazon ECR image management
- ✅ AWS EC2 deployment
- ✅ Self-hosted GitHub Actions runner
- ✅ Automated deployment health verification
- ✅ Containerized PostgreSQL
- ✅ Containerized Redis
- ✅ Containerized RabbitMQ

---

# 🔮 Future Improvements

The following improvements can be added in future versions:

- 🔒 HTTPS with a domain and load balancer
- 🔐 AWS Secrets Manager integration
- 📊 Centralized logging
- 📈 Prometheus / Grafana monitoring
- 💾 Automated database backups
- 🗃️ Automated database migrations
- 🔄 Blue/green or rolling deployments
- 🔍 Container vulnerability scanning
- 🏗️ Infrastructure as Code using Terraform

---

# 👩‍💻 Author

**Shweta Jaiswal**

B.Tech Computer Science Engineering  


### 🔗 Connect

- 💻 GitHub: [2004shweta](https://github.com/2004shweta)
- 📦 Project Repository: [payment-service-production](https://github.com/2004shweta/payment-service-production)

---

<p align="center">

⭐ If you found this project interesting, consider giving it a star!

</p>


### One important improvement I made

I removed the hardcoded deployed timestamp as something that looks like a permanent project value and kept it only as an **example response**. Your README now looks much more like a serious production/backend project rather than a basic college project.

After replacing the file:

cmd
git add README.md
git commit -m "Improve README documentation"
git push origin main

