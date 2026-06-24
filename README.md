# Quality Assurance Management Platform

A full-stack quality assurance and process management platform developed for higher education institutions.

The platform supports the complete quality lifecycle, including questionnaire management, process modeling, workflow assignment, evaluation tracking and continuous improvement.

Designed to support adaptable academic quality strategies and institutional performance monitoring.

---

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white) ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white) ![REST API](https://img.shields.io/badge/REST_API-02569B?style=for-the-badge) ![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black) ![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)

---

## Features

✔ Quality process management

✔ Questionnaire creation and evaluation

✔ Quality goal definition

✔ BPMN workflow modeling

✔ Historical comparison of evaluations

✔ Employee and team management

✔ Assignment of QA activities

✔ Notifications and monitoring

✔ Continuous improvement cycle

---

## System Overview

The platform enables universities to:

- Collect structured feedback
- Monitor institutional performance
- Define quality objectives
- Assign responsibilities
- Analyze historical trends
- Improve academic processes

---

## Architecture

Frontend

- HTML
- CSS
- JavaScript
- AJAX / Fetch API

↓

Backend

- Java 17
- Spring Boot

↓

Business Layer

- Controllers
- Services
- Entities
- Repositories

↓

Persistence Layer

- Spring Data JPA
- Hibernate

↓

Database

- PostgreSQL

---

## Technologies

### Backend
- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- Maven
- REST APIs

### Database
- PostgreSQL

### Frontend
- HTML
- CSS
- JavaScript

### Testing
- Postman

### Monitoring
- Spring Boot Actuator

---

## Core Modules

### Questionnaire Management

Create, activate and evaluate questionnaires used for collecting student feedback.

---

### Quality Goal Management

Define measurable quality objectives based on evaluation results.

---

### BPMN Workflow Modeling

Create and manage academic and administrative process diagrams.

---

### Historical Analytics

Track and compare quality performance across multiple periods.

---

### Team & Employee Management

Organize staff participation and assign responsibilities.

---

## User Roles

### QA Expert (Administrator)

- Manage questionnaires
- Create and publish BPMN workflows
- Assign processes to employees or groups
- Manage employees and groups
- Define quality objectives (KPIs)
- Monitor workflow execution
- Review evaluation results and historical comparisons
- Receive system notifications

### Employee

- Execute assigned tasks
- Participate in individual and group workflows
- Update task completion status
- Collaborate through process-specific chat
- Receive real-time notifications

### Student

- Complete evaluation questionnaires
- Submit feedback on academic processes
- Contribute data for quality assessment and KPI analysis

### Process Leader

- Coordinate assigned team workflows
- Monitor team task progress
- Participate in process discussions
- Oversee workflow completion within the assigned process

---

## Technical Concepts

- Layered Architecture
- MVC Pattern
- ORM Persistence
- Role-Based Access Control (RBAC)
- RESTful Communication

---

## API Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/bpmn` | Retrieve all BPMN workflow diagrams |
| `POST` | `/api/qa/assign` | Assign a workflow to an employee |
| `POST` | `/api/qa/assign-group` | Assign a workflow to an employee group |
| `GET` | `/api/employees` | Retrieve all employees |
| `GET` | `/api/forms` | Retrieve all evaluation forms |
| `POST` | `/api/responses` | Submit student evaluation responses |
| `GET` | `/api/objectives` | Retrieve quality objectives |
| `GET` | `/api/notifications` | Retrieve user notifications |
| `POST` | `/api/chat` | Send a chat message within a workflow |
| `POST` | `/api/bpmn/assign-status` | Update task assignment and completion status |

---

## Database

The application uses a relational database to store quality assurance workflows, users, evaluation data, BPMN diagrams, notifications, and process collaboration data.

### Main Entities / Tables

| Entity / Table | Description |
|---|---|
| `Employee` | Stores employee/user information such as full name, position, username, and password. |
| `EmployeeGroup` | Represents groups of employees used for group-based workflow assignments. |
| `BpmnDiagram` | Stores BPMN workflow diagrams, XML content, publication status, and task progress metadata. |
| `AssignedProcess` | Stores workflows assigned to individual employees. |
| `GroupAssignedProcess` | Stores workflows assigned to employee groups. |
| `TaskAssignmentStatus` | Tracks task assignments, assignees, completion status, and update timestamps. |
| `EvaluationForm` | Stores evaluation questionnaires and their active/inactive status. |
| `EvaluationResponse` | Stores student responses submitted for evaluation forms. |
| `QualityObjective` | Stores quality objectives, target values, and related KPI information. |
| `ProcessChatMessage` | Stores chat messages related to specific workflows/processes. |
| `ProcessComment` | Stores comments attached to processes. |
| `DeadlineNotification` | Stores deadline-related notifications for users. |

### Database Summary

- 12 main JPA entities
- 14 database tables
- Relational database design using PostgreSQL
- ORM mapping with Spring Data JPA / Hibernate
- Supports workflow tracking, evaluation management, notifications, chat, and KPI monitoring

---

## Installation

Clone repository

```bash
git clone https://github.com/MarMecha/QA-Project.git
```

Run backend

```bash
mvn spring-boot:run
```

Open application

```text
http://localhost:8080
```

---

## Screenshots

### QA Expert Dashboard

<img width="1907" height="865" alt="image" src="https://github.com/user-attachments/assets/8f6eb29e-a435-4fa2-a4cd-02c491fc39fc" />

### BPMN Modeling

<img width="1898" height="861" alt="image" src="https://github.com/user-attachments/assets/62987b7d-88b3-44cc-bb4c-d491d553dc59" />

### Questionnaire Management

<img width="1893" height="853" alt="image" src="https://github.com/user-attachments/assets/6bba3a6e-8936-41f3-bd7f-1493cb53e626" />

### Historical Analytics

<img width="1891" height="864" alt="image" src="https://github.com/user-attachments/assets/a84a3e1a-2eb0-4629-b44e-307f7748c0a3" />

---

## Future Improvements

- Docker containerization
- JUnit & Mockito tests
- Authentication hardening
- Swagger/OpenAPI
- Reporting Dashboard
- Advanced analytics

---


## Author

Mariol Mehalla

LinkedIn : www.linkedin.com/in/mariol-mehalla-49a866234

Email : mariosmi56@gmail.com
