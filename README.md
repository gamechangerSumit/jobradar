# JobRadar AI

Multi-category job discovery for Pune, nearby localities and other selectable cities. Built with React + Vite, Spring Boot 3 / Java 21, PostgreSQL, Flyway and Docker Compose.

## Features
- Search by role, company, skills, city, locality, category and employment type.
- Broad industry categories: IT, DevOps/cloud, support, business, finance, sales, healthcare, education, manufacturing, logistics, engineering, HR, hospitality, retail and more.
- Optional browser geolocation and radius filtering when listings have coordinates.
- Salary range extraction and min/max salary filters when the provider supplies salary data.
- Paginated discovery (20 results per page) and on-demand city scanning through the token-protected admin endpoint.
- User registration/login with BCrypt password hashing and JWT authentication.
- Per-account saved jobs and application status tracking (Interested, Applied, Interview, Offer, Rejected), persisted in PostgreSQL.
- Scheduled source collection, source/external-ID deduplication, Remote OK and Arbeitnow public feeds, plus optional Adzuna integration.
- Nginx reverse proxy, API health check and GitHub Actions CI.

## Run on Windows PowerShell
```powershell
git clone https://github.com/gamechangerSumit/jobradar.git
cd jobradar
Copy-Item .env.example .env
```
Set a strong POSTGRES_PASSWORD and a unique random JOBRADAR_JWT_SECRET (at least 32 characters) in .env, then run:
```powershell
docker compose up --build -d
docker compose ps
docker compose logs -f backend
```
Open dashboard at http://localhost:8081. API metadata: http://localhost:8081/api/meta. Health: http://localhost:8081/actuator/health.

Stop with `docker compose down`. To delete the database and all collected jobs, use `docker compose down -v`.

## Job sources
Remote OK and Arbeitnow public feeds are enabled by default. Remote OK jobs are global remote roles; Arbeitnow listings are mainly Europe-focused. JobRadar links to the original listing and displays source attribution. Always verify whether a remote job accepts applicants from India. Their public feeds are refreshed by the hourly collection schedule. See [Remote OK feed guidance](https://remoteok.com/faq) and [Arbeitnow API terms](https://www.arbeitnow.com/terms/).

## Real job data (Adzuna)
Register at https://developer.adzuna.com/, review the current terms and limits, and add credentials only to local .env:
```dotenv
ADZUNA_ENABLED=true
ADZUNA_APP_ID=your_app_id
ADZUNA_APP_KEY=your_app_key
ADZUNA_COUNTRY=in
ADZUNA_CITY=Pune
ADZUNA_RADIUS_KM=25
```
Then rebuild: `docker compose up --build -d`. Collection starts shortly after backend startup and repeats hourly. Adzuna is optional; Remote OK and Arbeitnow can provide remote/global vacancies without API credentials. Keep required Adzuna attribution and outbound links. Do not scrape or republish data unless source terms permit it.

## Manual collection
Set a long random JOBRADAR_ADMIN_TOKEN in .env and restart. You can use the dashboard's Scan city button and enter this token, or call the API directly. The endpoint is disabled if the token is unset. Keep it secret.

## API
- GET /api/meta — metadata and indexed job count.
- GET /api/jobs — keyword, city, category, employmentType, experience, postedWithinHours, coordinates, radius, salary, offset and limit.
- POST /api/admin/collect — manual collection; requires X-JobRadar-Admin-Token.
- POST /api/auth/register and POST /api/auth/login — account creation and sign-in.
- GET /api/workspace — authenticated saved-job IDs and application statuses.
- POST/DELETE /api/workspace/favorites/{jobId} — manage saved jobs.
- PUT/DELETE /api/workspace/applications/{jobId} — manage application status.

## Production checklist
Before public deployment add HTTPS, managed secrets, authentication rate limiting, email verification/password reset, restrictive CORS, private DB networking, backups, monitoring and provider compliance review. Use secure HttpOnly cookies rather than localStorage for browser token storage in a hardened production deployment. The project still needs a deployed runtime smoke test.
