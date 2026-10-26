# Pharmacy Digital Assistant — Backend

A Spring Boot REST API that helps a pharmacy keep track of its **patients, medications, prescriptions and inventory**, and automatically reminds patients before their medication runs out or their prescription expires.

The project follows a **hexagonal (ports and adapters) architecture**: the domain and use cases do not depend on Spring, JPA or the mail server, which are plugged in as adapters.

The web interface lives in [pharmacy-digital-assistant-client](https://github.com/eceakin/pharmacy-digital-assistant-client).

---

## Features

**Patients and medications**
- Patient records with contact details, status and SMS consent
- Medication tracking per patient: dosage, frequency, route, start and end dates
- Medication lifecycle: activate, hold, discontinue, complete
- Lists of medications that need a refill or are about to run out

**Prescriptions**
- E-prescriptions, paper prescriptions, reports and special prescriptions
- Validity tracking with expiring and expired lists
- Lifecycle actions: activate, expire, cancel, mark as used, use a refill

**Inventory**
- Product catalogue with barcode, category, manufacturer and pricing
- Batch-based stock with expiry dates, minimum stock levels and storage location
- Add, deduct and adjust stock; mark batches as expired or damaged
- Alerts for low stock, near-expiry and expired batches

**Notifications**
- Notifications by email, SMS or in-system channel, with priority and status tracking
- Real email delivery over SMTP; SMS sending is simulated and written to the log
- Send, retry and process scheduled notifications
- **Daily scheduled job** (09:00) that creates reminders for medications and prescriptions nearing their end date
- SMS is only used for patients who have given consent and have a phone number

**Settings**
- Warning thresholds (in days) for medication, prescription and stock expiry
- Notification time and email/SMS on-off switches
- Pharmacy name and contact details

## Architecture

```
            ┌───────────────────────────────────────────────┐
  HTTP ───► │ infrastructure/adapter/web   (REST controllers)│
            └──────────────────────┬────────────────────────┘
                                   │ input ports (use cases)
            ┌──────────────────────▼────────────────────────┐
            │ application   services, DTOs, mappers          │
            │ domain        models, value objects, enums,    │
            │               domain exceptions                │
            └──────────────────────┬────────────────────────┘
                                   │ output ports
      ┌────────────────────────────┼─────────────────────────┐
      ▼                            ▼                         ▼
 persistence adapter          email adapter            scheduler
 (JPA + PostgreSQL)           (Spring Mail / SMTP)     (@Scheduled)
```

```
src/main/java/com/pharmacy/assistant/
├── domain/
│   ├── model/              # Patient, Medication, Prescription, Product, Stock,
│   │                       # Notification, SystemSettings
│   ├── valueobject/        # Dosage, MedicationSchedule, ContactInfo, ProductPricing…
│   ├── enums/
│   └── exception/
├── application/
│   ├── port/input/         # Use case interfaces
│   ├── port/output/        # Repository and email interfaces
│   ├── service/            # Use case implementations
│   ├── dto/                # Request and response objects
│   └── mapper/
└── infrastructure/
    ├── adapter/web/        # REST controllers, ApiResponse, global exception handler
    ├── adapter/persistence/# JPA entities, Spring Data repositories, adapters
    ├── adapter/email/      # SMTP email service
    ├── scheduler/          # Daily notification job
    └── config/             # CORS
```

## Tech stack

- Java 17, Spring Boot 4
- Spring Web, Spring Data JPA, Bean Validation
- PostgreSQL
- Spring Mail (SMTP)
- Spring Scheduling
- Lombok
- Maven (wrapper included)

## Getting started

### Prerequisites

- JDK 17 or newer
- PostgreSQL
- An SMTP account for sending email (for Gmail, an [app password](https://support.google.com/accounts/answer/185833))

### 1. Create the database

```sql
CREATE DATABASE pharmacy_db;
```

The default configuration expects PostgreSQL on **port 5433**. Tables are created and updated automatically by Hibernate (`ddl-auto: update`).

### 2. Configure

Credentials are read from **environment variables**, so no secrets live in the repository. For local development, copy the example file and fill in your values:

```bash
cp .env.example .env
```

The application loads `.env` from the project root automatically, and `.env` is git-ignored. You can also set the same variables in your shell, IDE run configuration or deployment environment instead.

| Variable | Required | Default | Description |
|---|---|---|---|
| `DB_PASSWORD` | ✅ | — | PostgreSQL password |
| `MAIL_USERNAME` | ✅ | — | SMTP username (your email address) |
| `MAIL_PASSWORD` | ✅ | — | SMTP password (for Gmail, an app password) |
| `DB_URL` | | `jdbc:postgresql://localhost:5433/pharmacy_db` | JDBC URL |
| `DB_USERNAME` | | `postgres` | PostgreSQL user |
| `MAIL_FROM` | | value of `MAIL_USERNAME` | Sender and reply-to address |
| `MAIL_HOST` / `MAIL_PORT` | | `smtp.gmail.com` / `587` | SMTP server |
| `SERVER_PORT` | | `8080` | HTTP port |
| `CORS_ALLOWED_ORIGINS` | | `http://localhost:3000` | Comma-separated list of allowed frontend origins |

The remaining settings are in `src/main/resources/application.yml`.

### 3. Run

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

The API starts on **http://localhost:8080**. Check it with:

```bash
curl http://localhost:8080/api/health
```

## API

All endpoints are under `/api` and return a common wrapper:

```json
{
  "success": true,
  "message": "…",
  "data": { },
  "timestamp": "2025-01-01T09:00:00"
}
```

| Resource | Base path | Highlights |
|---|---|---|
| Patients | `/api/patients` | CRUD, search, active list, activate/deactivate, count |
| Medications | `/api/medications` | CRUD, by patient, refill-needed, expiring, activate/hold/discontinue/complete |
| Prescriptions | `/api/prescriptions` | CRUD, by number, by patient, expiring/expired, cancel, use refill |
| Products | `/api/products` | CRUD, by barcode, category, manufacturer, search, activate/discontinue |
| Stocks | `/api/stocks` | CRUD, by product, low/expiring/expired alerts, add, deduct, adjust |
| Notifications | `/api/notifications` | CRUD, by patient/status/type/channel, send, retry, process scheduled |
| Settings | `/api/settings` | Get, update, reset to defaults |
| Health | `/api/health` | Service status |

**Development helpers**

| Endpoint | Purpose |
|---|---|
| `GET /api/demo/check-medications` | Run the medication reminder check now |
| `GET /api/demo/check-prescriptions` | Run the prescription expiry check now |
| `GET /api/demo/check-all` | Run all checks now |
| `GET /api/test/email/simple?to=<address>` (also `/html`, `/medication`, `/prescription`) | Send a test email |

## Running tests

```bash
./mvnw test
```

## Related

- [pharmacy-digital-assistant-client](https://github.com/eceakin/pharmacy-digital-assistant-client): React web interface
