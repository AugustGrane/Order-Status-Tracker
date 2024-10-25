# Order Status Tracker

A full-stack application built with SvelteKit frontend and Spring Boot backend.

## Project Structure

```
.
├── frontend/          # SvelteKit frontend application
│   ├── src/          # Source files
│   ├── static/       # Static assets
│   └── ...
│
└── backend/          # Spring Boot backend application
    ├── src/          # Source files
    └── ...
```

## Setup Instructions

### Backend (Spring Boot)

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### Frontend (SvelteKit)

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend will start on `http://localhost:5173`

## Development

- Frontend development server: `http://localhost:5173`
- Backend API: `http://localhost:8080`

## Building for Production

### Backend
```bash
cd backend
./mvnw clean package
```

### Frontend
```bash
cd frontend
npm run build
