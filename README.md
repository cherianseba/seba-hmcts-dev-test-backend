# HMCTS Task Manager — Backend

A Spring Boot REST API for caseworker tasks. It provides validated task CRUD operations, status updates, and persists data in a local H2 database.

## Requirements

- JDK 21
- Git

The Gradle wrapper downloads and runs the configured Gradle version, so a separate Gradle installation is not required.

## Run locally on Windows

Open PowerShell in this repository and start the service:

```powershell
.\gradlew.bat bootRun
```

The API listens at <http://localhost:4000>. The default H2 file database is created in the repository directory as `hmcts-tasks.mv.db` and retains data between restarts. Do not commit the generated database file.

To build the application and run the default verification tasks:

```powershell
.\gradlew.bat build
```

Run the test source sets individually:

```powershell
.\gradlew.bat test
.\gradlew.bat integration
.\gradlew.bat functional
.\gradlew.bat smoke
```

## REST API

All task endpoints use the `/api/tasks` base path and JSON request/response bodies.

| Method | Endpoint | Purpose | Success response |
| --- | --- | --- | --- |
| `GET` | `/api/tasks` | List tasks ordered by due date | `200 OK`, JSON array |
| `POST` | `/api/tasks` | Create a task | `201 Created`, task JSON |
| `GET` | `/api/tasks/{id}` | Retrieve one task | `200 OK`, task JSON |
| `PUT` | `/api/tasks/{id}` | Replace task details | `200 OK`, updated task JSON |
| `PATCH` | `/api/tasks/{id}/status` | Change the task status | `200 OK`, updated task JSON |
| `DELETE` | `/api/tasks/{id}` | Delete a task | `204 No Content` |

### Create or update a task

`POST /api/tasks` and `PUT /api/tasks/{id}` accept:

```json
{
  "title": "Review case documents",
  "description": "Check the latest documents",
  "status": "OPEN",
  "dueDateTime": "2026-10-15T14:30:00"
}
```

- `title` is required, cannot be blank, and is limited to 160 characters.
- `description` is optional and limited to 4,000 characters.
- `status` is required and must be `OPEN`, `IN_PROGRESS`, or `COMPLETED`.
- `dueDateTime` is required and uses ISO local date-time format without a timezone suffix.

### Change task status

`PATCH /api/tasks/{id}/status` accepts:

```json
{
  "status": "COMPLETED"
}
```

Task responses include `id`, `title`, `description`, `status`, `dueDateTime`, `createdAt`, and `updatedAt`. Requests for unknown task IDs return `404 Not Found`; invalid request bodies are rejected by validation.

## Example requests

In PowerShell, create and list tasks with:

```powershell
$task = @{
  title = 'Review case documents'
  description = 'Check the latest documents'
  status = 'OPEN'
  dueDateTime = '2026-10-15T14:30:00'
} | ConvertTo-Json

$created = Invoke-RestMethod -Uri 'http://localhost:4000/api/tasks' `
  -Method Post -ContentType 'application/json' -Body $task

Invoke-RestMethod -Uri 'http://localhost:4000/api/tasks' -Method Get
```

## API documentation

When the service is running, Springdoc serves the interactive Swagger UI at <http://localhost:4000/swagger-ui/index.html>.

## Frontend

The companion frontend is available at [seba-hmcts-dev-test-frontend](https://github.com/cherianseba/seba-hmcts-dev-test-frontend). Start this backend first, then run the frontend; it connects to `http://localhost:4000` by default.
