Payment Service
A production-oriented payment service built with Java, Spring Boot, PostgreSQL, Redis, RabbitMQ, Docker, and AWS.
This project demonstrates backend development, containerization, asynchronous messaging, caching, database persistence, health monitoring, and automated CI/CD deployment.
Architecture
GitHub → GitHub Actions → AWS ECR → AWS EC2
                                      │
                              Docker Compose
                         ┌────────┬────┴────┬────────┐
                         │        │         │        │
                    Spring Boot PostgreSQL Redis RabbitMQ
Tech Stack
Backend
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Actuator
Database & Messaging
- PostgreSQL
- Redis
- RabbitMQ
DevOps & Cloud
- Docker
- Docker Compose
- GitHub Actions
- AWS EC2
- AWS ECR
- AWS IAM
- Ubuntu 24.04
Testing & API
- JUnit
- Spring Boot Test
- Testcontainers
- Postman
- Swagger / OpenAPI
Key Features
- RESTful payment APIs
- PostgreSQL persistence using Spring Data JPA
- Redis integration for caching
- RabbitMQ integration for asynchronous messaging
- Idempotency support for payment requests
- Payment audit records
- Input validation
- Production-specific Spring configuration
- Dockerized application
- Multi-stage Docker build
- Non-root Docker container
- Health monitoring using Spring Actuator
- Docker health checks
- Automated CI pipeline
- Automated AWS deployment
- AWS ECR container image storage
- Self-hosted GitHub Actions runner on EC2
Project Structure
payment-service/
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── deploy.yml
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-prod.yml
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── docker-compose.aws.yml
├── pom.xml
├── mvnw
└── README.md
Running Locally
Prerequisites
- Java 17
- Maven
- Docker Desktop
- Git
Clone the repository
git clone https://github.com/2004shweta/payment-service-production.git
cd payment-service-production
Start the application
docker compose up -d
Check containers:
docker compose ps
Health Check
curl http://localhost:8080/api/v1/health
Expected response:
{
  "service": "payment-service",
  "status": "UP"
}
Running with Maven
Run tests:
./mvnw clean test
Build the application:
./mvnw package -DskipTests
Windows:
mvnw.cmd clean test
Docker
Build the application image:
docker build -t payment-service:local .
Run using Docker Compose:
docker compose up -d
View logs:
docker compose logs -f payment-service
Stop services:
docker compose down
AWS Deployment
The application is deployed to AWS EC2 using Docker Compose.
AWS Components
- EC2
- ECR
- IAM
- Security Groups
- EBS
- Docker Compose
The EC2 instance runs Ubuntu and hosts the application containers.
CI/CD Pipeline
Every push to the main branch triggers GitHub Actions.
CI
1. Checkout source code.
2. Configure Java 17.
3. Run Maven tests.
4. Build the application.
Git Push → Checkout → Java 17 → Tests → Maven Build
Deployment
1. Run on the EC2 self-hosted GitHub Actions runner.
2. Authenticate with Amazon ECR.
3. Pull the latest Docker image.
4. Start/update Docker Compose services.
5. Run an application health check.
Git Push
   ↓
GitHub Actions
   ↓
EC2 Self-hosted Runner
   ↓
Amazon ECR
   ↓
Docker Compose
   ↓
Health Check
Health Monitoring
Health endpoint:
GET /api/v1/health
Example:
{
  "service": "payment-service",
  "status": "UP",
  "timestamp": "2026-10-05T14:28:05.994032844"
}
Spring Boot Actuator is also configured for application monitoring.
API Documentation
Swagger/OpenAPI:
http://localhost:8080/swagger-ui/index.html
For the deployed instance:
http://<EC2-PUBLIC-IP>:8080/swagger-ui/index.html
Database
PostgreSQL is the primary relational database.
The application maintains payment-related data including:
- Payments
- Payment audit records
- Idempotency keys
Persistence is handled using Spring Data JPA and Hibernate.
Redis
Redis is used for fast in-memory data access and caching.
RabbitMQ
RabbitMQ provides asynchronous messaging between application components.
Docker Security
The application Docker image uses a multi-stage build and runs the runtime container as a non-root user:
USER spring:spring
Environment Configuration
Local and production configuration are maintained separately:
application.yml
application-prod.yml
Production infrastructure connection details are supplied through environment variables.
Useful Commands
Check Docker containers:
docker ps
Check Docker Compose services:
docker compose ps
View application logs:
docker logs -f payment-service-app
Check application health:
curl http://localhost:8080/api/v1/health
Check GitHub Actions runner:
sudo ./svc.sh status
Restart GitHub Actions runner:
sudo ./svc.sh restart
CI/CD Result
The project supports:
- Automated testing
- Automated Maven builds
- Docker-based deployment
- Amazon ECR image management
- AWS EC2 deployment
- Self-hosted GitHub Actions runner
- Automated deployment health verification
Future Improvements
- HTTPS with a domain and load balancer
- AWS Secrets Manager integration
- Centralized logging
- Prometheus/Grafana monitoring
- Database backups
- Automated database migrations
- Blue/green or rolling deployments
- Container vulnerability scanning
- Infrastructure as Code using Terraform
Author
Shweta Jaiswal
B.Tech Computer Science Engineering
Lovely Professional University
GitHub: https://github.com/2004shweta