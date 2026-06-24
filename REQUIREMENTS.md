# Requirements

## Runtime

- Java 17
- PostgreSQL
- Maven Wrapper included in the repository:
  - Windows: `mvnw.cmd`
  - Linux/macOS: `./mvnw`

## Main Dependencies

The project uses Maven. Dependencies are defined in `pom.xml`.

- Spring Boot 3.5.3
- Spring Boot Web
- Spring Boot Data JPA
- Spring Boot Actuator
- PostgreSQL JDBC Driver
- Spring Boot Test

## Database

Create a PostgreSQL database before running the app:

```sql
CREATE DATABASE qa_project_db;
```

The local application config currently points to:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/qa_project_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

Do not commit real database passwords to GitHub. Use environment variables,
GitHub Actions secrets, or a local-only config file for private credentials.

## Build

```powershell
.\mvnw.cmd clean package
```

## Run

```powershell
.\mvnw.cmd spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

## Files To Include On GitHub

- `pom.xml`
- `mvnw`
- `mvnw.cmd`
- `.mvn/wrapper/maven-wrapper.properties`
- `src/`
- `.gitignore`
- `REQUIREMENTS.md`

Do not include:

- `target/`
- IDE folders such as `.idea/`, `.vscode/`, `.settings/`
- Local credentials or private database dumps
