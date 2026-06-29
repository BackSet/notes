# Project Context: Notes

## Project Overview
The `notes` project is a full-featured web application designed to allow users to create, organize, and manage their personal notes securely. It is built as a fullstack application with a clear separation between backend API and frontend client.

## Core Objectives
- Provide a responsive and intuitive user interface for managing notes.
- Secure user data using robust backend authentication and authorization.
- Ensure high performance, scalability, and ease of maintenance.
- Maintain comprehensive, automated tests for both backend and frontend.

## Tech Stack
- **Backend**: Spring Boot (Java 26, Maven, JPA, PostgreSQL, Flyway, Actuator, Security, Springdoc OpenAPI)
- **Frontend**: React (Vite, TypeScript, Tailwind CSS, TanStack Router, TanStack Query, Zustand, Sonner, openapi-fetch)
- **Infrastructure**: Docker Compose (PostgreSQL)

## Project State
- **Backend**: Initialized with Spring Boot, Java 26, and Maven. Core dependencies configured.
- **Frontend**: Initialized with React, Vite, TypeScript, and Tailwind CSS. Basic UI components and HTTP client configured.
- **Database**: PostgreSQL 16 ready to run via Docker Compose. Flyway integration configured.
- **CI/CD**: GitHub Actions workflow configured for building and testing both tiers.
- **Infrastructure & Deployment**: Prepared for Railway deployment. Multi-stage Dockerfiles, `railway.json` files, and a secure Caddyfile configuration are in place for production.

