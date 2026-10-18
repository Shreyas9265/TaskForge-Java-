# TaskForge

[![CI](https://github.com/Shreyas9265/taskforge/actions/workflows/ci.yml/badge.svg)](https://github.com/Shreyas9265/taskforge/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)
![React](https://img.shields.io/badge/React-18-61DAFB)
![React Native](https://img.shields.io/badge/React%20Native-Expo%2051-000020)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF)
![License](https://img.shields.io/badge/license-MIT-blue)

A secure, multi-platform task management system with JWT authentication: one Spring Boot REST API consumed by a **React web app**, a **React Native (Expo) mobile app**, and a **native Kotlin Android app (Jetpack Compose)**. Designed as a complete, production-style reference project — real auth, per-user data isolation, tests, CI, and Docker, not just a CRUD toy.

**Stack**
- Backend: Java 17, Spring Boot 3.3, Spring Security (JWT), Spring Data JPA, H2 (dev) / MySQL (prod), Maven
- Web: React 18, React Router, Axios
- Mobile (cross-platform): React Native 0.74 via Expo, React Navigation, Axios, Expo SecureStore
- Mobile (native Android): Kotlin, Jetpack Compose (Material 3), Retrofit + OkHttp, Coroutines, ViewModel
- Infra: Dockerfile for each service + docker-compose for one-command startup + GitHub Actions CI


---

## Resume / Portfolio Blurb

Feel free to use this as-is or adapt it:

> **TaskForge — Multi-Platform Task Management System** ([GitHub](https://github.com/Shreyas9265/taskforge))
> Designed and built a task management system with a Spring Boot REST API (JWT authentication, Spring Security, Spring Data JPA) shared by three clients: a React web app (Hooks, Context API, React Router), a cross-platform React Native (Expo) mobile app, and a native Kotlin Android app (Jetpack Compose, Retrofit, Coroutines, MVVM). Implemented per-user data isolation, server-side pagination and filtering, centralized exception handling, and a unit + integration test suite (JUnit 5, Mockito, MockMvc). Containerized with Docker and automated builds/tests via GitHub Actions CI.

Resume bullet points (pick 2–4):
- Built a multi-platform task management system: Spring Boot REST API with JWT auth, consumed by React (web), React Native (iOS/Android) and native Kotlin (Android) clients
- Designed REST API with pagination, filtering, and centralized validation/error handling using Spring Security and Spring Data JPA
- Developed a native Android client in Kotlin with Jetpack Compose, MVVM (ViewModel + Compose state), Retrofit/OkHttp and coroutines, including token-based session handling that returns the user to login when the session expires
- Built a React Native (Expo) mobile app with React Navigation, secure token storage (SecureStore), and shared API contract with the web client
- Wrote unit and integration tests (JUnit 5, Mockito, MockMvc) covering auth flows and cross-user access control
- Containerized backend, frontend, and MySQL with Docker Compose; automated build/test pipeline with GitHub Actions across Java, Node and Android

---

## Features

- User registration & login with JWT-based authentication
- Passwords hashed with BCrypt
- Per-user task ownership (users only see/edit their own tasks)
- Task CRUD: title, description, status (`TODO`/`IN_PROGRESS`/`DONE`), priority (`LOW`/`MEDIUM`/`HIGH`), due date
- Server-side pagination and status filtering
- Centralized exception handling with clean JSON error responses
- Bean validation on all inputs
- CORS configured for local frontend dev
- H2 in-memory DB for zero-setup local dev; MySQL profile for production
- Dockerized, with docker-compose bringing up MySQL + backend + frontend together
- Unit tests (Mockito) for service-layer logic + integration tests (MockMvc + H2) covering auth, validation, task CRUD, and cross-user data isolation
- Three clients on one API: React web, React Native (Expo) mobile, native Kotlin Android (Jetpack Compose)
- Secure token storage on mobile (Expo SecureStore) and automatic return-to-login when a session expires
- GitHub Actions CI pipeline: runs backend tests + packages the jar, builds the web app, bundles the React Native app, and assembles the Android APK, on every push/PR

---

## Project Structure

```
taskforge/
├── backend/                        Spring Boot REST API
│   ├── src/main/java/com/taskforge/
│   │   ├── config/                 Security config
│   │   ├── controller/             REST controllers (Auth, Task)
│   │   ├── dto/                    Request/response records
│   │   ├── entity/                 JPA entities (User, Task)
│   │   ├── exception/              Custom exceptions + global handler
│   │   ├── repository/             Spring Data JPA repositories
│   │   ├── security/                JWT filter, util, UserDetailsService
│   │   └── service/                 Business logic
│   ├── src/main/resources/application.yml
│   ├── src/test/java/com/taskforge/
│   │   ├── service/                  AuthServiceTest, TaskServiceTest (Mockito unit tests)
│   │   ├── controller/               AuthControllerIntegrationTest, TaskControllerIntegrationTest (MockMvc + H2)
│   │   └── TaskForgeApplicationTests.java
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                        React web SPA
│   ├── src/
│   │   ├── components/              Navbar, TaskForm, TaskItem, ProtectedRoute
│   │   ├── context/AuthContext.js   Global auth state
│   │   ├── pages/                   Login, Register, TaskDashboard
│   │   └── services/api.js          Axios instance + JWT interceptor
│   ├── Dockerfile
│   └── package.json
├── mobile-rn/                       React Native (Expo) app
│   ├── App.js                       Navigation (auth stack vs. task stack)
│   └── src/                         api.js, AuthContext.js, config.js, screens/, components/
├── mobile-android/                  Native Kotlin Android app (Jetpack Compose)
│   └── app/src/main/java/com/taskforge/mobile/
│       ├── data/                    Retrofit API, models, SessionStore
│       ├── ui/                      ViewModel + Compose screens
│       └── MainActivity.kt
├── docker-compose.yml
└── README.md
```

---

## Pushing to GitHub

This project already comes as an initialized git repo with one commit. To publish it:

1. Create a new **empty** repository on GitHub (no README/license/gitignore — this project already has them), e.g. named `taskforge`.
2. From inside the unzipped `taskforge` folder:
   ```bash
   git remote add origin https://github.com/Shreyas9265/taskforge.git
   git push -u origin main
   ```
3. Update the CI badge URL near the top of this README with your actual username.
4. (Optional) Add topics on GitHub — `java`, `spring-boot`, `react`, `jwt`, `full-stack` — so it's discoverable.

## Running in VS Code (step by step)

1. **Unzip and open the folder**: `code taskforge` (or File → Open Folder in VS Code, select the `taskforge` folder — the one containing `backend/`, `frontend/`, `docker-compose.yml`).
2. **Install the recommended extensions** — VS Code will prompt you automatically (an `.vscode/extensions.json` is included). If it doesn't, install manually:
   - **Extension Pack for Java** (`vscjava.vscode-java-pack`)
   - **Spring Boot Extension Pack** (`vmware.vscode-spring-boot`)
   - **ES7+ React/Redux/React-Native snippets** (optional, for frontend)
3. **Run the backend**:
   - Open `backend/src/main/java/com/taskforge/TaskForgeApplication.java`
   - Click the **Run** ▷ button that appears above `public static void main(...)`, or press `F5`
   - A launch config is already provided in `.vscode/launch.json` — just pick "Run TaskForge Backend" from the Run and Debug panel
   - Or from a terminal: `cd backend && mvn spring-boot:run`
   - It starts on **http://localhost:8080** using the in-memory H2 database — no setup needed
4. **Run the frontend** (in a second terminal):
   ```bash
   cd frontend
   npm install
   npm start
   ```
   Opens automatically at **http://localhost:3000**.
5. **Try it**: register a user in the UI, log in, create a few tasks. Everything is wired end to end.

If you'd rather run tests first to confirm everything's healthy: `cd backend && mvn test`.

## Running the Mobile Apps

Start the backend first (`cd backend && mvn spring-boot:run`, port 8080). Both mobile apps talk to it directly.

| Where the app runs | Backend URL to use |
|---|---|
| Android emulator | `http://10.0.2.2:8080` (default in both apps) |
| iOS simulator | `http://localhost:8080` (default in the React Native app) |
| Physical phone | `http://<your-computer-LAN-IP>:8080` (phone and computer on the same Wi-Fi) |

### React Native (Expo) — `mobile-rn/`

Requires Node 18+. Install **Expo Go** on your phone, or use an Android emulator / iOS simulator.

```bash
cd mobile-rn
npm install
npx expo start            # press "a" for Android emulator, "i" for iOS simulator, or scan the QR code with Expo Go
```

On a physical phone, point it at your computer instead of the emulator default:

```bash
EXPO_PUBLIC_API_URL=http://192.168.1.20:8080 npx expo start      # macOS / Linux
$env:EXPO_PUBLIC_API_URL="http://192.168.1.20:8080"; npx expo start   # Windows PowerShell
```

### Native Kotlin Android — `mobile-android/`

Requires **Android Studio** (Koala or newer) with an Android SDK and an emulator.

1. In Android Studio choose **Open** and select the `mobile-android` folder. Let Gradle sync finish (first sync downloads dependencies).
2. Pick an emulator (Device Manager → Create Device) and press **Run ▶**.
3. For a physical phone, change `API_BASE_URL` in `mobile-android/app/build.gradle.kts` to `http://<your-LAN-IP>:8080/`.

Command line (needs Gradle 8.7+ installed): `cd mobile-android && gradle assembleDebug`.

> The mobile apps use plain `http://` for local development only (`usesCleartextTraffic` is enabled in the Android manifest). Use HTTPS and disable that flag before any real deployment.

---

## Running Locally (without Docker)

### 1. Backend

Requires Java 17+ and Maven.

```bash
cd backend
mvn spring-boot:run
```

The API starts on **http://localhost:8080** using an in-memory H2 database (profile `dev`, active by default).
H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taskforgedb`, user `sa`, blank password).

### 2. Frontend

Requires Node.js 18+.

```bash
cd frontend
npm install
npm start
```

The app starts on **http://localhost:3000** and proxies API calls to `http://localhost:8080`.

---

## Running Tests

```bash
cd backend
mvn test
```

This runs:
- **Unit tests** — `AuthServiceTest`, `TaskServiceTest` (Mockito, no Spring context, fast)
- **Integration tests** — `AuthControllerIntegrationTest`, `TaskControllerIntegrationTest` (full Spring context, real HTTP requests via MockMvc against an in-memory H2 database), covering:
  - Register → login round trip
  - Duplicate username/email rejection
  - Bean validation errors (400s)
  - Bad credentials (401)
  - Task create/read/update/delete
  - **Cross-user isolation** — verifying user B cannot read or see user A's tasks
  - Requests without a token being rejected (401)

CI runs this automatically on every push via `.github/workflows/ci.yml`.

## Running with Docker Compose (full stack + MySQL)

```bash
docker-compose up --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- MySQL: localhost:3306 (db `taskforgedb`, user `root`, password `password`)

---

## API Reference

### Auth

| Method | Endpoint             | Body                                          | Auth |
|--------|-----------------------|------------------------------------------------|------|
| POST   | `/api/auth/register`  | `{ username, email, password }`                | No   |
| POST   | `/api/auth/login`     | `{ username, password }`                       | No   |

Both return: `{ token, username, email, role }`. Use the token as `Authorization: Bearer <token>` on all subsequent requests.

### Tasks (all require `Authorization: Bearer <token>`)

| Method | Endpoint          | Description                                   |
|--------|-------------------|------------------------------------------------|
| GET    | `/api/tasks`      | List current user's tasks. Query params: `status`, `page`, `size`, `sort` |
| GET    | `/api/tasks/{id}` | Get one task                                    |
| POST   | `/api/tasks`      | Create a task: `{ title, description, status, priority, dueDate }` |
| PUT    | `/api/tasks/{id}` | Update a task                                   |
| DELETE | `/api/tasks/{id}` | Delete a task                                   |

### Example: register + create a task

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"secret123"}'

# copy the returned token, then:
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"title":"Write report","priority":"HIGH","status":"TODO"}'
```

---

## Notes on Production Readiness

- Switch to the `prod` Spring profile (`SPRING_PROFILES_ACTIVE=prod`) to use MySQL instead of H2.
- Set `JWT_SECRET` to a long, random value via environment variable — never rely on the default in `application.yml` outside local dev.
- `ddl-auto` is `update` in dev and `validate` in prod; use a migration tool (Flyway/Liquibase) for real production schema management.
- Add rate limiting / refresh tokens if this were to go beyond a demo.

## Possible Extensions

- Refresh tokens + token blacklist on logout
- Role-based admin endpoints (bulk view of all users' tasks)
- Task comments / attachments
- Email notifications for due dates
- Unit + integration tests (JUnit, Mockito, Testcontainers) for services/controllers
- CI pipeline (GitHub Actions) running `mvn test` and `npm test` on push
