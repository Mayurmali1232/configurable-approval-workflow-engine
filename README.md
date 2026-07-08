# Configurable Approval Workflow Engine (Backend)

An enterprise-grade, database-driven workflow state machine built using **Java 21**, **Spring Boot 4.1.0**, and **Spring Security**. This system dynamically drives approval paths (such as Leave and Expense requests) completely from database configurations without any hardcoded if-else logic for role and step tracking transitions.


===============================================================================================================================================================================================================================================================================================================

## 🛠️ Architecture Design & Strategy

### 1. Core Principle: Database-Driven State Machine
Traditional workflow engines rely heavily on hardcoded switch-cases or if-else rules based on request types or user roles. 
This project eliminates that by storing the workflow sequence mapping directly inside the database configuration rules. 
- The engine dynamically reads the active `currentStep` from the request.
- It fetches the corresponding role verification row criteria from the database.
- It dynamically computes the next stage or marks the workflow state as fully finalized.

### 2. Transaction Management & Auditing
Every workflow transition runs under strict Spring `@Transactional` semantics. If an approval succeeds, 
the engine simultaneously updates the request tracking status pointer and appends an immutable log f
ootprint entry to the system history registry table in a single atomic transaction context.

===========================================================================================================================================================================================================================================================================

## Features

- JWT Authentication & Authorization
- Role-Based Access Control
- Configurable Workflow from Database
- Dynamic Multi-Step Approval Process
- Leave and Expense Workflow Support
- Admin Override Functionality
- Immutable Approval History
- Transaction Management
- Request Validation
- Global Exception Handling
- H2 Database
- Unit Testing (80%+ Coverage)

===========================================================================================================================================================================================================================================================================

## Technology Stack

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA
- Hibernate
- H2 Database
- JWT Authentication
- Maven
- JUnit 5
- Mockito
- JaCoCo


===========================================================================================================================================================================================================================================================================


## 📊 Database Schema Layout (H2 In-Memory)

The system relies on three tightly coupled structural domain model tracks:

* **User (`users`):** Stores authentication details, roles (`REQUESTER`, `APPROVER`, `ADMIN`), and encrypted credentials.
* **Request (`requests`):** Tracks the live lifecycle metadata state, current processing index step, tracking tags, and author.
* **ApprovalStep (`approval_steps`):** The engine config matrix defining required authority sequence orders dynamically by entity request category.
* **ApprovalHistory (`approval_history`):** The immutable audit tracking log layer capturing timestamps, actors, and step transition justifications.


===========================================================================================================================================================================================================================================================================


## Project Structure

```
approval-workflow-engine/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.workflow.engine/
│   │   │       ├── config/              # Spring configuration classes
│   │   │       ├── constants/           # Application constants and enums
│   │   │       ├── controller/          # REST API controllers
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       ├── entity/              # JPA Entity classes
│   │   │       ├── exception/           # Custom exceptions & global exception handler
│   │   │       ├── mapper/              # Entity-DTO mapping classes
│   │   │       ├── repository/          # Spring Data JPA repositories
│   │   │       ├── security/            # JWT, Security configuration & filters
│   │   │       ├── service/             # Service interfaces
│   │   │       ├── service/impl/        # Service implementations
│   │   │       └── ApprovalWorkflowEngineApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── data.sql
│   │       └── schema.sql
│   │
│   └── test/
│       └── java/
│           └── com.workflow.engine/
│               ├── controller/
│               ├── service/
│               ├── repository/
│               └── security/
│
├── target/                             # Compiled classes & reports
├── Dockerfile                          # Docker configuration
├── pom.xml                             # Maven dependencies
├── mvnw
├── mvnw.cmd
├── HELP.md
└── README.md



====================================================================================================================================

## Database Tables

### Request

- id
- type
- status
- createdBy
- createdAt

### ApprovalStep

- id
- requestType
- stepOrder
- role

### ApprovalHistory

- id
- requestId
- action
- actionBy
- actionAt


===========================================================================================================================================================================================================================================================================

## 📊 5. Workflow Configuration

In this project, how a request moves from one person to another is **100% controlled by the database**. There are no hardcoded `if-else` or `switch-case` statements in the Java code for different request types.

### 💡 Examples of How It Works

#### 1. LEAVE Request Workflow
When an employee applies for a leave, the system dynamically routes it through these steps:
* **Step 1:** Must be approved by a user with the role  `MANAGER`
* **Step 2:** Must be approved by a user with the role  `HR`

#### 2. EXPENSE Request Workflow
When someone submits a travel expense bill, the system automatically checks the database and enforces these steps:
* **Step 1:** Must be approved by a user with the role  `TEAM_LEAD`
* **Step 2:** Must be approved by a user with the role  `FINANCE`

===========================================================================================================================================================================================================================================================================

## Security

- JWT Authentication
- Bearer Token Authorization
- Password Encryption using BCrypt
- Role-Based Authorization

Roles

- REQUESTER
- APPROVER
- ADMIN


===============================================================================================================================================================================================================================================================================================================

## Workflow Rules

- Requester cannot approve their own request.
- Only the assigned approver can approve the current step.
- Admin can override any request.
- Invalid state transitions are rejected.
- Every workflow action is transactional.
- Complete approval history is maintained.


===============================================================================================================================================================================================================================================================================================================

## API Endpoints

| Method | Endpoint | Allowed Role | Description |
|--------|----------|--------------|-------------|
| **POST** | `/api/v1/auth/register` | Public (No Token) | Creates a new user account in the system. |
| **POST** | `/api/v1/auth/login` | Public (No Token) | Authenticates user credentials and returns a JWT token. |
| **POST** | `/api/v1/requests` | REQUESTER | Creates a new request (e.g., **LEAVE**) and starts Step 1 of the workflow. |
| **GET** | `/api/v1/requests/{id}` | REQUESTER, APPROVER, ADMIN | Fetches request details along with the current active approval step. |
| **POST** | `/api/v1/requests/approve{id}` | APPROVER | Approves the current step and moves the request to the next approval step or marks it as completed. |
| **POST** | `/api/v1/requests/{id}/reject` | APPROVER | Rejects the request and immediately terminates the workflow. |
| **GET** | `/api/v1/requests/history/{id}` | REQUESTER, ADMIN | Retrieves the complete immutable approval history of the request. |


===============================================================================================================================================================================================================================================================================================================


## 🚀 Step-by-Step API Execution Order

The application is pre-seeded with functional test profiles (`mayur` as Requester, `ganesh` as Approver, and `rushi` as Admin). All credentials use the raw value `"password"`.

### 1. Authenticate & Obtain Requester Token
* **Endpoint:** `POST /api/v1/auth/login`
* **Payload:**
    ```json
    { "username": "mayur", "password": "password" }
    ```
* *Action:* Copy the authorization JWT string returned from the response.

===============================================================================================================================================================================================================================================================================================================


### 2. Initialize a Leave Request Lifecycle
* **Endpoint:** `POST /api/v1/requests`
* **Header:** `Authorization: Bearer <MAYUR_TOKEN>`
* **Payload:**
    ```json
    { "type": "LEAVE" }
    ```
* *Action:* Note down the returned request ID (Value: `1`).


===============================================================================================================================================================================================================================================================================================================


### 3. Switch Context to Approver Session
* **Endpoint:** `POST /api/v1/auth/login`
* **Payload:**
    ```json
    { "username": "ganesh", "password": "password" }
    ```
* *Action:* Copy the newly generated approver token string.


===============================================================================================================================================================================================================================================================================================================


### 4. Process Workflow Authorization (Step 1 Sign-off)
* **Endpoint:** `POST /api/v1/requests/1/approve`
* **Header:** `Authorization: Bearer <GANESH_TOKEN>`
* **Payload:**
    ```json
    { "remarks": "Documents verified." }
 
 ===============================================================================================================================================================================================================================================================================================================
  

### 5. Switch Context to Administrative Override Session
* **Endpoint:** `POST /api/v1/auth/login`
* **Payload:**
    ```json
    { "username": "rushi", "password": "password" }
   
* *Action:* Copy the admin token string.

===============================================================================================================================================================================================================================================================================================================


### 6. Process Step 2 and Finalize Lifecycle Tracks
* **Endpoint:** `POST /api/v1/requests/1/approve`
* **Header:** `Authorization: Bearer <RUSHI_TOKEN>`
* **Payload:**
    ```json
    { "remarks": "Final executive authorization clearance complete." }
    ```
    
===============================================================================================================================================================================================================================================================================================================
    

### 7. Retrieve the Complete Immutable Audit Trail
* **Endpoint:** `GET /api/v1/requests/1/history`
* **Header:** `Authorization: Bearer <ANY_VALID_TOKEN>`

===============================================================================================================================================================================================================================================================================================================

# API FLOW

User Login
      ↓
Receive JWT Token
      ↓
Create Request
      ↓
Load Approval Steps from Database
      ↓
Approve Step 1
      ↓
Approve Step 2
      ↓
Completed

OR

Reject
      ↓
Rejected

===============================================================================================================================================================================================================================================================================================================

## Validation

- Request type cannot be empty.
- Required fields are validated using Jakarta Validation.
- Invalid requests return appropriate HTTP status codes.

===============================================================================================================================================================================================================================================================================================================

## Global Exception Handling

The application handles:

- Resource Not Found
- Invalid Workflow State
- Unauthorized Access
- Validation Errors
- Duplicate Resources
- Internal Server Errors

===============================================================================================================================================================================================================================================================================================================

## H2 Console

URL

http://localhost:8080/h2-console

Driver

org.h2.Driver

JDBC URL

jdbc:h2:mem:workflowdb

Username

sa

Password

(password if configured)


===============================================================================================================================================================================================================================================================================================================


## 🧪 Testing and Coverage Execution Details

The project incorporates automated test suites built via **JUnit 5** and **Mockito** frameworks to validate edge-case 
constraint parameters, authorization blocks, and happy path progressions.

* To run the tests and verify execution footprints: **Right-click project -> Coverage As -> JUnit Test**.
* **Code Coverage Track Performance:** Exceeds the **80% requirement threshold** for the entire core processing service 
layer block matrix.

===============================================================================================================================================================================================================================================================================================================

## 🚀 Run Instructions
1. Open the project inside Spring Tool Suite (STS) or IntelliJ IDEA.
2. Run `ApprovalWorkflowEngineApplication.java` as a **Spring Boot App**.
3. The built-in programmatic database data seeder will automatically inject user testing context records (`mayur`, `ganesh`, `rushi`) upon bootstrap initialization.

## 🧪 Running Unit Tests
- Right-click the project folder -> select **Coverage As** -> **JUnit Test**.
- Active testing footprint coverage tracks exceed the targeted 80% criteria floor.


====================================================================================================================================

## 🌐 Live API Testing Guide (Using Render Cloud URL)

The application is deployed live on Render's Docker infrastructure. Any user can test the complete end-to-end **`LEAVE`** workflow by executing the following steps in Postman.

### 📡 Base Live URL
```text
[https://configurable-approval-workflow-engine.onrender.com](https://configurable-approval-workflow-engine.onrender.com)


[ Note on Render's Free Tier: If the server hasn't received a request in 15 minutes, it goes to sleep. Your first request might take 60-90 seconds to wake up the server. Subsequent requests will respond instantly! or try again same request]

Step-by-Step Execution Order
1. Authenticate & Obtain Requester Token
Method: POST

URL: https://configurable-approval-workflow-engine.onrender.com/api/v1/auth/login

Body (raw -> JSON):

JSON
{ "username": "mayur", "password": "password" }
Action: Click Send. Copy the string inside the "token" field from the response.

*******************************************************************


2. Initialize a New Leave Request
Method: POST

URL: https://configurable-approval-workflow-engine.onrender.com/api/v1/requests

Headers: Add Key: Authorization, Value: Bearer <PASTE_MAYUR_TOKEN>

Body (raw -> JSON):

JSON
{ "type": "LEAVE" }
Action: Click Send. Note the returned request ID (it will be 1).

*******************************************************************


3. Fetch Request Details (View Current Step State)
Method: GET

URL: https://configurable-approval-workflow-engine.onrender.com/api/v1/requests/1

Headers: Add Key: Authorization, Value: Bearer <PASTE_MAYUR_TOKEN>

Action: Click Send. It will display the live record showing "currentStep": 1 and "status": "PENDING".

*******************************************************************


4. Switch Session to Approver (ganesh)
Method: POST

URL: https://configurable-approval-workflow-engine.onrender.com/api/v1/auth/login

Body (raw -> JSON):

JSON
{ "username": "ganesh", "password": "password" }
Action: Click Send. Copy the newly generated approver token string.

*******************************************************************


5. Approve Step 1
Method: POST

URL: https://configurable-approval-workflow-engine.onrender.com/api/v1/requests/1/approve

Headers: Add Key: Authorization, Value: Bearer <PASTE_GANESH_TOKEN>

Body (raw -> JSON):

JSON
{ "remarks": "Documents verified. Advancing lifecycle to Admin." }
Action: Click Send. The response will show "currentStep" advanced to 2.

*******************************************************************

6. Switch Session to Admin (rushi)
Method: POST

URL: https://configurable-approval-workflow-engine.onrender.com/api/v1/auth/login

Body (raw -> JSON):

JSON
{ "username": "rushi", "password": "password" }
Action: Click Send. Copy the generated admin token string.


*******************************************************************

7. Final Step Approval (Sign-off)
Method: POST

URL: https://configurable-approval-workflow-engine.onrender.com/api/v1/requests/1/approve

Headers: Add Key: Authorization, Value: Bearer <PASTE_RUSHI_TOKEN>

Body (raw -> JSON):

JSON
{ "remarks": "Final executive authorization clearance complete." }
Action: Click Send. The response status will instantly switch to "status": "APPROVED".

*******************************************************************

8. Retrieve Complete Immutable Audit Trail
Method: GET



URL: https://configurable-approval-workflow-engine.onrender.com/api/v1/requests/history/1

Headers: Add Key: Authorization, Value: Bearer <ANY_VALID_TOKEN>

Action: Click Send. It returns the complete list of actions tracking exactly who created and approved the record with time parameters.


===============================================================================================================================================================================================================================================================================================================
## Author

Mayur Mali

Backend Developer

GitHub: https://github.com/Mayurmali1232/configurable-approval-workflow-engine
Render: https://configurable-approval-workflow-engine.onrender.com


===============================================================================================================================================================================================================================================================================================================
