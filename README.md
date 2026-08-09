# 🚀 CrediHub

### Full-Stack Loan Management Platform

CrediHub is a full-stack digital loan management platform built with **Java 17**, **Spring Boot**, **Spring Security**, **Spring Data JPA**, **Thymeleaf**, **MySQL**, **Spring Cloud OpenFeign**, **Stripe**, and **Microsoft Azure**.

The platform covers the complete loan lifecycle — from loan calculation and application submission to application review, loan creation, repayment scheduling, and installment payments.

---

## 🎓 Evaluation / Live Demo

The application is deployed and ready to use:

**Live Demo:**  
https://credihub-dhcmagcdhpb8dca7.uaenorth-01.azurewebsites.net/

No Stripe account or Stripe configuration is required from the evaluator.

The live environment is already configured with the required application, database, Payment Service, and Stripe settings.

### Administrator account

```text
Email:
admin@credihub.com
```

The administrator password is provided separately for evaluation and is not stored in the repository.
For the purpose of the live demo, a demo admin password will be provided
```text
Password:
Admin123!
```
---

# 📖 About the Project

CrediHub simulates a digital lending platform where users can:

- Register and authenticate
- Manage their profile
- Calculate loan repayments
- Submit loan applications
- Track application status
- View approved loans
- View repayment schedules
- Pay installments
- Track their remaining loan balance

The application also provides role-based administration for:

- Reviewing applications
- Approving and rejecting applications
- Managing users and moderator roles
- Managing loan products

Payment functionality is separated into a dedicated **Payment Service microservice**.

### Payment Service repository

https://github.com/elitsa7/credihub-payment-service

---

# ✨ Main Features

## 👤 Guest Users

Unauthenticated visitors can:

- View the home page
- Use the loan calculator
- Register
- Login

## 👥 Registered Users

Authenticated users can:

- View and edit their profile
- Submit loan applications
- Edit pending applications
- Cancel pending applications
- View applications and application details
- View approved loans
- View rejected loans
- View repayment schedules
- Track installment statuses
- Pay the next available installment
- Track remaining loan balance

## 🛡️ Moderator

Moderators can:

- View submitted applications
- Review application details
- Approve pending applications
- Reject pending applications

## 👑 Administrator

Administrators can:

- Review applications
- Approve and reject applications
- Manage users
- Promote users to Moderator
- Remove Moderator roles
- Manage loan products
- Edit loan products
- Activate and deactivate loan products

---

# 🔐 Authentication & Authorization

CrediHub uses **Spring Security** with role-based authorization.

Supported roles:

```text
USER
 └── Customer functionality

MODERATOR
 ├── Customer functionality
 └── Application review

ADMIN
 ├── Customer functionality
 ├── Application review
 ├── User management
 └── Loan product management
```

Passwords are encoded using Spring Security's password encoder.

---

# 🔑 Administrator Initialization

A default administrator is initialized when the application starts if the configured administrator does not already exist.

Administrator email:

```text
admin@credihub.com
```

The password is supplied through:

```text
ADMIN_PASSWORD
```

Example:

```text
ADMIN_PASSWORD=your-secure-password
```

The password is intentionally kept outside the source code.

---

# 🔗 Payment Service

The dedicated microservice is available in a separate repository:

**https://github.com/elitsa7/credihub-payment-service**

It is responsible for:

- Loan accounts
- Installments
- Payments
- Payment gateways
- Stripe Checkout
- Stripe webhooks
- Payment state management
- Overdue installment processing

---

# 🔗 Communication Between Services

CrediHub communicates with the Payment Service through **Spring Cloud OpenFeign**.

Main operations include:

```text
CrediHub
   │
   ├── Create Loan Account
   ├── Get User Loans
   ├── Get Loan Installments
   └── Create Checkout Session
             │
             ▼
      Payment Service
```

Internal requests include:

```text
X-API-KEY
```

The shared key is configured through:

```text
PAYMENT_SERVICE_API_KEY
```

---

# 🔄 Loan Lifecycle

```text
Register / Login
       │
       ▼
Loan Calculator
       │
       ▼
Submit Application
       │
       ▼
PENDING
       │
       ▼
Moderator / Admin Review
       │
       ├──────────────► REJECTED
       │
       ▼
APPROVED
       │
       ▼
Loan Account Created
       │
       ▼
Installments Generated
       │
       ▼
Installment Becomes Payable
       │
       ▼
Payment
       │
       ▼
Installment PAID
       │
       ▼
Remaining Balance Updated
       │
       ▼
Next Installment
       │
       ▼
All Installments Paid
       │
       ▼
Loan CLOSED
```

---

# 🧮 Loan Calculator

The home page contains a loan calculator.

Users select:

- Loan product
- Requested amount
- Repayment period

The calculator displays:

- Annual interest rate
- Monthly payment
- Total repayment amount

The requested amount and repayment period are validated against the selected loan product.

---

# 📝 Loan Applications

Users can submit applications for active loan products.

Applications contain information such as:

- Requested amount
- Repayment period
- Monthly income
- Loan purpose
- Selected loan product

New applications start with:

```text
PENDING
```

Pending applications can be edited or cancelled by their owner.

---

# 🛡️ Application Review

Moderators and administrators can review submitted applications.

Application status flow:

```text
PENDING
   │
   ├── APPROVED
   │
   └── REJECTED
```

When an application is approved:

1. The application status becomes `APPROVED`.
2. CrediHub requests a loan account from the Payment Service.
3. The Payment Service creates the loan account.
4. The repayment schedule is generated.
5. Installments become available according to the schedule.

---

# 💰 Loan Accounts

The Payment Service creates a loan account after application approval.

A loan account contains information such as:

- Application ID
- User ID
- Principal amount
- Remaining balance
- Annual interest rate
- Monthly payment
- Period in months
- Start date
- End date
- Paid installment count
- Loan status

A newly created loan starts as:

```text
ACTIVE
```

---

# 📅 Repayment Schedule

Installments are generated automatically when a loan account is created.

Each installment contains:

- Installment number
- Amount
- Due date
- Status
- Payment date

Installment statuses include:

```text
PENDING
DUE
PAID
OVERDUE
```

Only the first pending installment can be paid.

---

# 💳 Payment Modes

The Payment Service uses profile-specific payment and webhook gateways.

```text
PaymentGateway
      │
      ├── MockPaymentGateway
      │       └── dev / test
      │
      └── StripePaymentGateway
              └── prod


StripeWebhookGateway
      │
      ├── MockStripeWebhookGateway
      │       └── dev / test
      │
      └── StripeWebhookGatewayImpl
              └── prod
```

---

## 🖥️ Local Development Payments

Local development uses:

```text
MockPaymentGateway
```

Therefore, local development does **not** require:

- A Stripe account
- Stripe API keys
- A Stripe webhook
- Stripe test cards

When a local installment is paid, the Payment Service completes the payment locally and updates the installment and loan balance.

---

## 🌐 Live Demo Payments

The deployed production environment uses:

```text
StripePaymentGateway
```

Stripe is already configured in the live environment.

The evaluator does **not** need to create a Stripe account or configure Stripe.

The production flow is:

```text
User
 │
 ▼
CrediHub
 │
 ▼
Payment Service
 │
 ▼
Stripe Checkout
 │
 ▼
Stripe Webhook
 │
 ▼
Payment Service
 │
 ▼
Installment = PAID
 │
 ▼
Loan balance updated
```

---

# 💳 Stripe Test Cards

For a Stripe test-mode payment, use:

### Successful payment

```text
4242 4242 4242 4242
```

Expiry: any future date  
CVC: any 3 digits

### Declined payment

```text
4000 0000 0000 9995
```

### 3D Secure authentication

```text
4000 0025 0000 3155
```

These are Stripe test cards and do not charge real money.

> The test cards apply only when the deployed Stripe integration is running in Stripe test mode.

---

# 🔔 Stripe Webhook

The Payment Service exposes:

```text
POST /api/v1/payments/webhook
```

Stripe sends Checkout events to this endpoint.

The webhook signature is verified before the event is processed.

The application handles:

```text
checkout.session.completed
```

After successful payment:

1. Payment becomes `SUCCESS`.
2. Payment timestamp is stored.
3. Installment becomes `PAID`.
4. Installment payment date is stored.
5. Paid installment count is increased.
6. Remaining loan balance is reduced.
7. When all installments are paid, the loan becomes `CLOSED`.

---

# ⏰ Scheduled Processing

CrediHub contains a loan application scheduler that runs hourly.

The Payment Service contains an installment scheduler that runs daily at midnight.

The Payment Service scheduler checks installment due dates and marks overdue installments accordingly.

---

# ⚡ Caching

CrediHub caches loan products.

The Payment Service caches:

```text
userLoans
loanInstallments
```

Relevant caches are evicted when loan or payment data changes.

---

# 🛠️ Technology Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.0 |
| Security | Spring Security |
| ORM | Hibernate / Spring Data JPA |
| Database | MySQL |
| Frontend | Thymeleaf |
| UI | Bootstrap 5 |
| Styling | CSS3 |
| Microservice Communication | Spring Cloud OpenFeign |
| Payments | Stripe Java SDK |
| Build | Maven |
| Testing | JUnit 5, Mockito, MockMvc |
| Test Database | H2 |
| Coverage | JaCoCo |
| Cloud | Microsoft Azure App Service |
| CI/CD | GitHub Actions |

---

# 📸 Screenshots
## 🏠 Home Page

<details>
<summary>📷 View Screenshot</summary>

![home.png](screenshots/home.png)
![Home Page](screenshots/home2.png)

</details>

## 🧮 Loan Calculator

<details>
<summary>📷 View Screenshot</summary>

![Loan Calculator](screenshots/calculator.png)

</details>

## 📝 Create Loan Application

<details>
<summary>📷 View Screenshot</summary>

![Create Loan Application](screenshots/create-application.png)

</details>

## 📋 My Applications

<details>
<summary>📷 View Screenshot</summary>

![My Applications](screenshots/my-applications.png)

</details>

## 🔎 Application Details

<details>
<summary>📷 View Screenshot</summary>

![Application Details](screenshots/application-details.png)

</details>

## 💰 My Loans

<details>
<summary>📷 View Screenshot</summary>

![My Loans](screenshots/my-loans.png)

</details>

## 📅 Repayment Schedule

<details>
<summary>📷 View Screenshot</summary>

![Repayment Schedule](screenshots/repayment-schedule.png)

</details>

## 💳 Stripe Checkout

<details>
<summary>📷 View Screenshot</summary>

![Stripe Checkout](screenshots/stripe.png)
![Successful Payment](screenshots/stripe-success.png)

</details>

## 👑 Administration Home Page

<details>
<summary>📷 View Screenshot</summary>

![Administration](screenshots/home-admin.png)

</details>

## 🛡️ Review Applications

<details>
<summary>📷 View Screenshot</summary>

![Review Applications](screenshots/review-applications.png)

</details>

## 👥 User Management

<details>
<summary>📷 View Screenshot</summary>

![User Management](screenshots/roles.png)

</details>

## 🏦 Loan Products

<details>
<summary>📷 View Screenshot</summary>

![Loan Products](screenshots/products.png)

</details>

---

# ⚙️ Spring Profiles

Available profiles:

```text
dev
test
prod
```

---

# 🖥️ Local Setup
## 1. Clone CrediHub

```bash
git clone https://github.com/elitsa7/credihub.git
cd credihub
```

## 2. Clone Payment Service

In another terminal:

```bash
git clone https://github.com/elitsa7/credihub-payment-service.git
cd credihub-payment-service
```

---

## 3. Configure CrediHub

The local `dev` profile uses:

```text
Host: localhost
Port: 3306
Database: credihub
Username: root
```

Set:

```text
DB_PASSWORD
```

The Payment Service defaults to:

```text
PAYMENT_SERVICE_URL=http://localhost:8081
```

and:

```text
PAYMENT_SERVICE_API_KEY=test
```

The `application-properties` profile uses:

The administrator password is configured using:

```text
ADMIN_PASSWORD
```

Example:

```text
DB_PASSWORD=your-mysql-password
ADMIN_PASSWORD=your-admin-password
PAYMENT_SERVICE_URL=http://localhost:8081
PAYMENT_SERVICE_API_KEY=test
```

---

## 4. Configure Payment Service

The local `dev` profile uses:

```text
Host: localhost
Port: 3306
Database: credihub_payment
Username: root
```

Set:

```text
DB_PASSWORD
PAYMENT_SERVICE_API_KEY=test
```

No Stripe configuration is required for local development.

---

## 5. Start Payment Service

```bash
cd credihub-payment-service
mvn clean install
mvn spring-boot:run
```

Payment Service:

```text
http://localhost:8081
```

---

## 6. Start CrediHub

In the main project:

```bash
cd credihub
mvn clean install
mvn spring-boot:run
```

CrediHub:

```text
http://localhost:8080
```

---

# 🧪 Local Payment Testing

With the default `dev` profile:

```text
MockPaymentGateway
```

is used.

No Stripe account, keys, webhook or test card is needed.

The local payment process completes through the mock gateway and updates:

```text
Payment → SUCCESS
Installment → PAID
Loan balance → Updated
```

---

# 🧪 Testing

The project includes:

- Unit tests
- Controller/API tests
- Integration tests
- Security tests

Technologies used:

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- H2
- JaCoCo

Run tests:

```bash
mvn test
```

Generate the JaCoCo report:

```bash
mvn verify
```

---

# ☁️ Azure Deployment

The live CrediHub application runs on **Microsoft Azure App Service**.

Deployment is automated using GitHub Actions.

The pipeline:

```text
Git Push
    │
    ▼
GitHub Actions
    │
    ├── Checkout
    ├── Setup Java 17
    ├── Maven Build
    ├── Package JAR
    └── Upload Artifact
            │
            ▼
      Azure App Service
```

---

# 📡 Payment Service API

The Payment Service exposes:

### Loan Accounts

```text
GET  /api/v1/loan-accounts/user/{userId}
POST /api/v1/loan-accounts
```

### Installments

```text
GET  /api/v1/installments/loan/{loanAccountId}
POST /api/v1/installments/{installmentId}/checkout
```

### Stripe Webhook

```text
POST /api/v1/payments/webhook
```

For complete microservice documentation:

https://github.com/elitsa7/credihub-payment-service

---

# 🔮 Future Improvements

Possible future improvements:

- Email notifications
- PDF loan contracts
- Advanced administration dashboards
- Payment history export
- Additional payment providers
- Two-factor authentication
- More detailed analytics
- Expanded reporting

---

# 🔗 Related Project

### CrediHub Payment Service

https://github.com/elitsa7/credihub-payment-service

---

# 👩‍💻 Author

**Elitsa Dacheva**

Java / Spring Boot Developer

---

<div align="center">

### 🚀 CrediHub

**Digital Loan Management with Spring Boot & Microservices**

</div>
