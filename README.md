# JobRadar AI

Multi-category job discovery for Pune, nearby localities and other selectable cities. Built with React + Vite, Spring Boot 3 / Java 21, PostgreSQL, Flyway and Docker Compose.

## Features
- Search by role, company, skills, city, locality, category and employment type.
- Broad industry categories: IT, DevOps/cloud, support, business, finance, sales, healthcare, education, manufacturing, logistics, engineering, HR, hospitality, retail and more.
- Optional browser geolocation and radius filtering when listings have coordinates.
- Salary range extraction and min/max salary filters when the provider supplies salary data.
- Paginated discovery (20 results per page) and on-demand city scanning through the token-protected admin endpoint.
- Saved jobs and application status tracking (Interested, Applied, Interview, Offer, Rejected). These are stored in this browser's local storage and are not synced between devices/users.
- Scheduled source collection, source/external-ID deduplication and optional Adzuna integration.
- Token-protected manual collection endpoint.
- Nginx reverse proxy, API health check and GitHub Actions CI.

## Run on Windows PowerShell
```powershell
git clone https://github.com/gamechangerSumit/jobradar.git
cd jobradar
Copy-Item .env.example .env
```
Set a strong local `POSTGRES_PASSWORD` in `.env`, then run:
```powershell
docker compose up --build -d
docker compose ps
docker compose logs -f backend
```
Open dashboard at http://localhost:8081. API metadata: http://localhost:8081/api/meta. Health: http://localhost:8081/actuator/health.

Stop with `docker compose down`. To delete the database and all collected jobs, use `docker compose down -v`.

## Real job data (Adzuna)
Register at https://developer.adzuna.com/, review the current terms and limits, and add credentials only to local `.env`:
```dotenv
ADZUNA_ENABLED=true
ADZUNA_APP_ID=your_app_id
ADZUNA_APP_KEY=your_app_key
ADZUNA_COUNTRY=in
ADZUNA_CITY=Pune
ADZUNA_RADIUS_KM=25
```
Then rebuild: `docker compose up --build -d`. Collection starts shortly after backend startup and repeats hourly. Adzuna is the only implemented live source; with no provider configured the app runs but will not show imported vacancies. Keep required Adzuna attribution and outbound links. Do not scrape or republish data unless source terms permit it.

## Manual collection
Set a long random `JOBRADAR_ADMIN_TOKEN` in `.env` and restart. You can use the dashboard's **Scan city** button and enter this token, or call the API directly. For example, scan Pune:
```powershell
$headers = @{ "X-JobRadar-Admin-Token" = "your-token" }
Invoke-RestMethod -Method Post -Uri 'http://localhost:8081/api/admin/collect?city=Pune' -Headers $headers
```
The endpoint is disabled if the token is unset. Keep it secret.

## API
- `GET /api/meta` — metadata and indexed job count.
- `GET /api/jobs` — keyword, city, category, employmentType, experience, postedWithinHours, latitude, longitude, radiusKm, minSalary, maxSalary, offset and limit (max 100 per request).
- `POST /api/admin/collect` — manual collection; requires `X-JobRadar-Admin-Token`.

## Add another source
Implement the Spring `JobSourceConnector` interface as a component. Return normalized `JobListing` records with stable source-specific IDs. Use only APIs/feeds whose terms permit this use.

## Production checklist
Before public deployment add user authentication, per-user saved-job/application persistence, HTTPS, managed secrets, rate limiting, restrictive CORS, private DB networking, backups, monitoring and provider compliance review. Browser-local workspace data is not a multi-user account system.
