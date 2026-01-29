# Clinical API - Spring Boot Application

A Spring Boot REST API for managing patient clinical data. This application provides comprehensive CRUD operations for patients and their clinical records with comprehensive test coverage and API documentation.

## Project Overview

The Clinical API is a microservice built with Spring Boot that manages:
- **Patient Management**: Create, read, update, and delete patient records
- **Clinical Data Tracking**: Store and manage clinical measurements and test results for patients
- **RESTful Endpoints**: Full REST API with standardized HTTP methods
- **Interactive API Documentation**: Swagger UI for exploring and testing endpoints
- **Code Quality Metrics**: JaCoCo code coverage reports

## Technology Stack

- **Java 25**: Programming language
- **Spring Boot 4.0.2**: Application framework
- **Spring Data JPA**: Database access layer
- **MySQL 8**: Relational database
- **Springdoc OpenAPI 2.2.0**: API documentation and Swagger UI
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking library for tests
- **JaCoCo**: Code coverage tool
- **Maven**: Build and dependency management

## Project Structure

```
clinicalsapi/
├── src/
│   ├── main/
│   │   ├── java/com/ankit/patientclinicals/clinicalsapi/
│   │   │   ├── controllers/          # REST API endpoints
│   │   │   │   ├── PatientController.java
│   │   │   │   └── ClinicalDataController.java
│   │   │   ├── models/               # JPA entities
│   │   │   │   ├── Patient.java
│   │   │   │   └── ClinicalData.java
│   │   │   ├── repos/                # Data access layer
│   │   │   │   ├── PatientRepository.java
│   │   │   │   └── ClinicalDataRepository.java
│   │   │   ├── dtos/                 # Data transfer objects
│   │   │   │   └── ClinicalDataDTO.java
│   │   │   └── ClinicalsapiApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/ankit/patientclinicals/clinicalsapi/
│           ├── PatientControllerTest.java
│           ├── ClinicalDataControllerTest.java
│           └── ClinicalsapiApplicationTests.java
├── pom.xml
└── README.md
```

## API Endpoints

### Patient Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patients` | Get all patients |
| GET | `/api/patients/{id}` | Get patient by ID |
| POST | `/api/patients` | Create new patient |
| PUT | `/api/patients/{id}` | Update patient |
| DELETE | `/api/patients/{id}` | Delete patient |

### Clinical Data Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/clinicaldata` | Get all clinical data records |
| GET | `/api/clinicaldata/{id}` | Get clinical data by ID |
| POST | `/api/clinicaldata` | Create new clinical data record |
| PUT | `/api/clinicaldata/{id}` | Update clinical data record |
| DELETE | `/api/clinicaldata/{id}` | Delete clinical data record |
| POST | `/api/clinicaldata/save` | Save clinical data for a specific patient (using DTO) |

## Prerequisites

- Java 25 or higher
- Maven 3.6.0 or higher
- MySQL 8.0 or higher
- Git

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd clinicalsapi
```

### 2. Configure Database

Update the database connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinicals
spring.datasource.username=root
spring.datasource.password=root123
```

### 3. Create Database (Optional)

```sql
CREATE DATABASE IF NOT EXISTS clinicals;
```

The application will automatically create tables on startup using JPA's `ddl-auto=update` setting.

## Running the Application

### Start the Application

```bash
.\mvnw spring-boot:run
```

The application will start on `http://localhost:8080/patientservices`

## Running Tests

### Run All Tests

```bash
.\mvnw test
```

### Run Specific Test Class

```bash
.\mvnw test -Dtest=PatientControllerTest
.\mvnw test -Dtest=ClinicalDataControllerTest
```

### Run Tests with Output

```bash
.\mvnw test -X
```

## Code Coverage Reports

### Generate JaCoCo Coverage Report

```bash
.\mvnw clean verify
```

Or explicitly:

```bash
.\mvnw clean test jacoco:report
```

### View Coverage Report

1. Open your browser
2. Navigate to: `file:///C:/Codebase/copilotForJava/clinicalsapi/target/site/jacoco/index.html`

The report displays:
- **Overall line coverage percentage**
- **Per-package breakdown** (controllers, models, repos, DTOs)
- **Per-class metrics** with detailed coverage
- **Line-by-line highlighting** (green = covered, red = uncovered)
- **Branch coverage analysis**

## API Documentation (Swagger UI)

### View Interactive API Documentation

1. Start the application (see "Running the Application" section)
2. Open your browser and navigate to:

```
http://localhost:8080/patientservices/swagger-ui.html
```

### View OpenAPI Specification (JSON)

```
http://localhost:8080/patientservices/api-docs
```

In Swagger UI, you can:
- View all available endpoints
- See request/response schemas
- Test endpoints directly with sample data
- View parameter descriptions and requirements

## Example API Requests

### Create a Patient

```bash
curl -X POST http://localhost:8080/patientservices/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "age": 30
  }'
```

### Get All Patients

```bash
curl http://localhost:8080/patientservices/api/patients
```

### Create Clinical Data for a Patient

```bash
curl -X POST http://localhost:8080/patientservices/api/clinicaldata/save \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "componentName": "Blood Pressure",
    "componentValue": "120/80"
  }'
```

## Database Schema

### Patient Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| age | INT | NOT NULL |

### ClinicalData Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| component_name | VARCHAR(255) | NOT NULL |
| component_value | VARCHAR(255) | NOT NULL |
| measured_date_time | TIMESTAMP | NOT NULL |
| patient_id | BIGINT | FOREIGN KEY, NOT NULL |

## Test Coverage

The project includes comprehensive unit tests for all controllers:

- **PatientControllerTest**: 6 test methods covering CRUD operations
- **ClinicalDataControllerTest**: 8 test methods covering CRUD operations and custom endpoints

All tests use:
- **Mockito** for mocking repositories
- **MockMvc** for testing HTTP endpoints
- **JUnit 5** for test execution
- **Assertions** for validating HTTP status codes and response data

## Build and Deploy

### Clean Build

```bash
.\mvnw clean install
```

### Create JAR File

```bash
.\mvnw package
```

The JAR file will be created in the `target/` directory.

### Run JAR File

```bash
java -jar target/clinicalsapi-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### Port Already in Use

If port 8080 is already in use, change it in `application.properties`:

```properties
server.port=8081
```

### Database Connection Error

- Ensure MySQL is running
- Verify database credentials in `application.properties`
- Check if the database exists or if JPA has permission to create tables

### JaCoCo Report Not Generating

- Ensure tests are passing: `.\mvnw test`
- Check for Java version compatibility (Java 25 recommended)
- Clear target directory: `.\mvnw clean`

### Swagger UI Not Loading

- Verify the application is running on the correct port
- Check that Springdoc OpenAPI dependency is properly configured in `pom.xml`
- Ensure context path is correct: `/patientservices`

## Contributing

1. Create a feature branch
2. Make your changes
3. Add/update tests as needed
4. Run `.\mvnw clean verify` to ensure tests pass and coverage is maintained
5. Commit and push your changes

---

**Last Updated**: January 28, 2026
