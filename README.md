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

===============================================================================================================================================================================================================================================================================================================

## 📊 Database Schema Layout (H2 In-Memory)

The system relies on three tightly coupled structural domain model tracks:

* **User (`users`):** Stores authentication details, roles (`REQUESTER`, `APPROVER`, `ADMIN`), and encrypted credentials.
* **Request (`requests`):** Tracks the live lifecycle metadata state, current processing index step, tracking tags, and author.
* **ApprovalStep (`approval_steps`):** The engine config matrix defining required authority sequence orders dynamically by entity request category.
* **ApprovalHistory (`approval_history`):** The immutable audit tracking log layer capturing timestamps, actors, and step transition justifications.

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