# JobRadar AI
Multi-category job discovery for Pune, nearby localities and other selectable cities.

## Stack
- React + Vite dashboard
- Spring Boot 3 / Java 21 REST API
- PostgreSQL + Flyway migrations
- Docker Compose
- Hourly collection scheduler and provider connector interface

## Run locally
1. Copy `.env.example` to `.env` and set a local database password.
2. Run `docker compose up --build`.
3. Dashboard: http://localhost:8081
4. API metadata: http://localhost:8080/api/meta
5. Health: http://localhost:8080/actuator/health

## Search API
`GET /api/jobs` supports keyword, city, category, employmentType, experience, postedWithinHours, latitude, longitude, radiusKm and limit.

Examples:
- `/api/jobs?city=Pune&category=DevOps&postedWithinHours=168`
- `/api/jobs?city=Hinjewadi&keyword=technician`
- `/api/jobs?latitude=18.5204&longitude=73.8567&radiusKm=10`

## Live data
The scheduled collector and `JobSourceConnector` interface are ready, but no live provider is enabled yet. Implement a provider using an API, RSS/feed, or company career integration whose terms permit collection. Each record should have a stable external ID and normalized location/category. Do not assume unrestricted scraping or fabricate vacancies.

Radius filtering requires coordinates on each listing; text-based city/locality search works without geocoding. Add a permitted geocoder to enrich records when necessary.

## Production
Add authentication, HTTPS, secrets management, rate limiting, restrictive CORS and private database networking before public deployment.