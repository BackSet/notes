# Notes Fullstack Project

This project is a fullstack notes application built with:
- **Backend**: Spring Boot (Java 26, Maven, JPA, PostgreSQL, Flyway, Actuator, Security)
- **Frontend**: React (Vite, TypeScript, Tailwind CSS, TanStack Router, TanStack Query, Zustand, Sonner, openapi-fetch)
- **Infrastructure**: Docker Compose (PostgreSQL)

## Project Structure

```
/
├── backend/            # Spring Boot backend
├── frontend/           # React + Vite frontend
├── docs/ai/            # AI-targeted documentation
├── docker-compose.yml  # Docker Compose for PostgreSQL
├── .env.example        # Root environment variables template
└── README.md           # This file
```

## Getting Started

### Prerequisites
- Java 26
- Maven 3.9+
- Node.js 18+ & npm
- Docker & Docker Compose

### 1. Database Setup
Start the PostgreSQL database using Docker Compose:
```bash
docker compose up -d
```

### 2. Backend Setup
1. Copy the environment template:
   ```bash
   cp backend/.env.example backend/.env
   ```
2. Build and run the Spring Boot application:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   The backend will be available at `http://localhost:8080`.

### 3. Frontend Setup
1. Copy the environment template:
   ```bash
   cp frontend/.env.example frontend/.env
   ```
2. Install dependencies and run in development mode:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   The frontend will be available at `http://localhost:5173`.
