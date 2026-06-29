# Naming Conventions & Patterns

## General Rules
- Use English for all code symbols (classes, variables, functions, tables, columns).
- Use Spanish or English for documentation as requested by the user.

## Backend (Java / Spring Boot)
- **Classes**: PascalCase (e.g., `NoteController`, `NoteService`).
- **Methods & Variables**: camelCase (e.g., `createNote()`, `updatedAt`).
- **Packages**: lowercase, dot-separated (e.g., `com.notes.backend.controller`).
- **JPA Entities**: PascalCase matching table name in singular (e.g., `Note` maps to `notes` table).
- **Database Tables**: snake_case plural (e.g., `notes`, `users`).
- **Database Columns**: snake_case singular (e.g., `title`, `created_at`).

## Frontend (React / TypeScript)
- **Components**: PascalCase (e.g., `Button.tsx`, `NoteCard.tsx`).
- **Hooks**: camelCase starting with `use` (e.g., `useNotes.ts`).
- **Utilities & Files**: camelCase (e.g., `client.ts`, `utils.ts`).
- **CSS Classes**: Tailwind classes in `className` attribute.
- **Zustand Stores**: camelCase starting with `use` (e.g., `useAuthStore`).
- **Routes**: kebab-case for directories and files (e.g., `dashboard/index.tsx`, `notes/$noteId.tsx`).
