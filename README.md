# Glorify-Arranger

A choir stage arrangement optimizer built with **Java Spring Boot** (backend) and **Next.js** (frontend).

## Project Structure

- `backend/` - Spring Boot API that parses Excel and returns arrangement plans.
- `frontend/` - Next.js SPA for uploading an Excel file, setting row count, and rendering the stage layout.

## Backend (Java)

### Run locally

```bash
cd backend
mvn spring-boot:run
```

### API

- `POST /api/arrange` (multipart/form-data)
  - `file`: Excel file (`.xlsx`)
  - `rows`: target row count (integer)

Returns JSON with seat coordinates for rendering.

### Docker

```bash
docker build -t glorify-arranger-backend ./backend
docker run -p 8080:8080 glorify-arranger-backend
```

## Frontend (Next.js)

### Run locally

```bash
cd frontend
npm install
npm run dev
```

### Docker

```bash
docker build -t glorify-arranger-frontend ./frontend
docker run -p 3000:3000 glorify-arranger-frontend
```

## Deployment Suggestions

- **Frontend**: Deploy to **Vercel** (static Next.js app). Just connect the repo and configure build command `npm run build`.
  - 참고 파일: `frontend/vercel.json` (기본 환경 변수 설정)
- **Backend**: Deploy to **Render.com** / **Railway.app** using the provided `Dockerfile`.
  - 참고 파일: `render.yaml` (Render 배포 설정 예시)

## Notes

- The arrangement logic supports a **dynamic row count** and attempts to keep part zones separated (bass/tenor vs alto/soprano).
- If a part cannot fit within the requested rows, excess members are returned in `unplacedMembers`.

