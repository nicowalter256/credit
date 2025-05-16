# Loan Enablement Microservices for SMEs — Metropol CRB Simulation

## Overview of your solution

This project implements a secure, microservice-based system simulating Metropol's credit platform, designed to support Small and Medium Enterprises (SMEs) in their loan lifecycle. The platform enables customers to seamlessly manage their loan activities, while providing system administrators with the tools to oversee and control the loan approval process.

Key functionalities include:

**For Customers (SMEs):**
-   **Loan Application:** Customers can register for the service and apply for new loans through a straightforward process.
-   **Loan Repayment:** The system facilitates making payments towards outstanding loans.
-   **Loan Monitoring:** Customers can view their loan repayment history and check their current loan balances

**For System Users (e.g., Loan Officers, Administrators):**
-   **Loan Management:** Authorized system users have the capability to review loan applications and take actions such as:
    -   Approving loan requests.
    -   Rejecting loan requests.
    -   Cancelling existing loans.

**Security and Access Control:**
-   The platform incorporates a robust role-based access control (RBAC) mechanism.
-   Critical loan management actions (approval, rejection, cancellation) are restricted to authorized system users with appropriate permissions.
-   Ordinary customers do not have access to these administrative functionalities, ensuring a secure and controlled environment.

## Technologies Used

This project leverages a modern stack of technologies to deliver a robust and secure loan enablement platform for Metropol CRB Simulation:

- **Testing:**
    -   **JUnit 5:** The primary framework for writing unit tests, ensuring individual components function as expected.
    -   **Mockito:** Used extensively for creating mock objects, allowing for isolated unit testing by simulating dependencies.
    -   **MockMvc (Spring Test):** Employed for testing Spring MVC controllers and REST API endpoints without needing a full HTTP server, facilitating robust integration tests at the controller level.

- **Application Security & Authentication:**
    -   **Spring Security:** The core framework for implementing comprehensive security measures, including authentication and authorization for API endpoints.
    -   **JWS (JSON Web Signature) / JWT:** Utilized for creating and validating secure, stateless access tokens (JWTs), which are fundamental to the authentication process.
    -   **RSA Encryption (Public/Private Key):** Implemented for asymmetric encryption, particularly for securing sensitive data like access tokens, enhancing their confidentiality and integrity during transmission and storage.

- **Database:**
    -   **PostgreSQL:** Serves as the primary relational database for storing critical application data, including customer profiles, loan applications, repayment records, and credit limits.

- **Containerization:**
    -   **Docker:** Used for containerizing the microservices. This ensures consistency across different environments (development, testing, production) and simplifies deployment and scaling.

- **API Documentation:**
    -   **Swagger (OpenAPI):** Integrated to generate interactive and comprehensive API documentation. This allows developers and consumers to easily understand, explore, and test the RESTful services.

- **Core Platform & Utilities:**
    -   **Programming Language:** Java 17
    -   **Framework:** Spring Boot 2.7.18
        -   **Spring Web:** For building RESTful APIs.
        -   **Spring Data JPA:** For simplified data access and interaction with the PostgreSQL database.
        -   **Spring Validation:** For validating request data using `javax.validation` annotations.
        -   **Spring Async:** For handling asynchronous operations (e.g., customer registration, loan request processing) to improve responsiveness and throughput.
    -   **Build Tool:** Apache Maven (for project build, dependency management, and packaging).
    -   **Lombok:** To reduce boilerplate code in Java classes (e.g., getters, setters, constructors).
    -   **Structured Logging (Log4j2):** Used for effective application monitoring, debugging, and auditing with structured output, typically configured via a `log4j2.xml` file.

## How to Set Up and Run the Application Locally

This project is designed to be run using Docker, which simplifies the setup and ensures a consistent environment.

### Prerequisites

Before you begin, ensure you have the following installed on your system:

-   **Java Development Kit (JDK):** While the application runs inside a Docker container, having a JDK installed locally can be useful for development or if you need to interact with Java tools directly. Version 17 is used by the project.
-   **Docker:** Docker is essential for building and running the containerized application and its database. Download and install Docker Desktop from the official Docker website.

### Setup and Execution Steps

1.  **Navigate to the Docker Directory:**
    Open your terminal or command prompt and change your directory to the `docker` folder located within the project's root directory.

2.  **Run the Build Script:**
    Execute the `build_containers.sh` script. This script automates the entire setup process:
    ```bash
    ./build_containers.sh
    ```
    This script will perform the following actions:
    *   **Build Docker Containers:** It builds the necessary Docker images for the Java application and the PostgreSQL database.
    *   **Java Application Container:**
        *   Clones the latest source code automatically from the GitHub repository.
        *   Performs all necessary setup steps, including compiling the application and installing dependencies, without requiring manual intervention.
    *   **PostgreSQL Database Container:**
        *   Initializes the database.
        *   Runs initial SQL scripts located in the `init-db` folder (found within the `docker` directory). These scripts are crucial for creating custom PostgreSQL types that the Java application requires for correct database schema setup and column definitions.

3.  **Application Availability:**
    Once the script completes successfully and the containers are up and running, the Java application will be accessible.

### Accessing the Application

You can access the loan enablement microservice via your API client at:
`http://localhost:8085`

This is the port on which the Java application listens for incoming requests.

## PostgreSQL Database Configuration

The application utilizes a PostgreSQL database, which is also managed and run as a Docker container as part of the local setup process.

### Dockerized Configuration

The primary configuration for the PostgreSQL database (including credentials and database name) is defined within the Docker environment, typically in a `docker-compose.yaml` file located in the `docker` directory. The `build_containers.sh` script orchestrates the setup based on this file.

When the PostgreSQL container starts, the following are typically configured via environment variables within the `docker-compose.yaml` file for the PostgreSQL service:
-   **Database Name:** The name of the database the application will connect to.
-   **Username:** The PostgreSQL user for the application.
-   **Password:** The password for the PostgreSQL user.

These credentials are then used by the Java application (configured in its `application.properties` or `application.yml` file) to establish a connection with the database container.

### Database Initialization

As mentioned in the "Setup and Execution Steps," initial SQL scripts located in the `docker/init-db/` folder are automatically executed when the PostgreSQL container is first created. These scripts are responsible for:
-   Creating the database if it doesn't exist.
-   Setting up any required custom PostgreSQL types.
-   Potentially creating initial tables or schema structures.
