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

## Adzuna live job source
1. Register for an API ID and key at https://developer.adzuna.com/.
2. Add them to `.env` as `ADZUNA_APP_ID` and `ADZUNA_APP_KEY`.
3. Set `ADZUNA_ENABLED=true`, then rebuild with `docker compose up --build -d`.
4. The collector queries Pune and a configurable nearby radius once per hour, normalizes categories, and stores results by stable source ID.

The Adzuna connector is disabled until credentials are supplied. The official API is intended for evaluation on its free tier; confirm your account limits and current terms. Displayed Adzuna listings must retain the required Adzuna attribution and outbound link.

## Search API
`GET /api/jobs` supports keyword, city, category, employmentType, experience, postedWithinHours, latitude, longitude, radiusKm and limit. Radius filtering requires listing coordinates; text-based city/locality search remains available.

## More sources
Implement additional permitted API/RSS/company-career providers through `JobSourceConnector`. Do not assume unrestricted scraping or fabricate vacancies.

## Production
Add authentication, HTTPS, secrets management, rate limiting, restrictive CORS and private database networking before public deployment.