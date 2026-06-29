# Module Map: Notes

## Project Structure
```
/
├── backend/                  # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/notes/backend/    # Java source files
│   │   │   └── resources/                 # Config & Migrations
│   │   └── test/                          # Unit & integration tests
│   └── pom.xml               # Maven configuration
│
├── frontend/                 # React application
│   ├── src/
│   │   ├── components/       # UI components
│   │   ├── lib/              # Utilities & API client
│   │   ├── routes/           # Routing configuration (TanStack Router)
│   │   └── store/            # State management (Zustand)
│   └── package.json          # Node dependencies
│
└── docs/                     # Documentation
    └── ai/                   # AI-targeted guidelines
```

## Backend Package Structure (com.notes.backend)
- `config/`: Application configuration (Security, CORS, OpenAPI).
- `controller/`: REST controllers exposing API endpoints.
- `model/`: JPA entities representing the database schema.
- `repository/`: Spring Data JPA repositories for database access.
- `service/`: Business logic implementations.
- `dto/`: Data Transfer Objects for API requests and responses.
- `security/`: JWT and security filter implementations.

## Frontend Directory Structure (frontend/src)
- `components/ui/`: Atomic, reusable UI components (Button, Input, etc.).
- `lib/api/`: Central HTTP client using `openapi-fetch`.
- `lib/utils.ts`: Helper functions (e.g. class merger `cn`).
- `routes/`: Routing structure for TanStack Router.
- `store/`: Zustand stores for global state management.
