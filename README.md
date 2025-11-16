\# Retail Online Shopping POC



\## Overview

This is a proof-of-concept retail online shopping platform with:

\- \*\*Frontend:\*\* React + Tailwind CSS

\- \*\*Backend:\*\* Spring Boot

\- \*\*Database:\*\* PostgreSQL

\- \*\*Analytics:\*\* Snowflake (CSV ingestion from Postgres)



The POC demonstrates basic product listing, cart management, checkout, and data pipeline to analytics.



---



\## Repository Structure



\- `frontend/` - React app with Tailwind

\- `backend/` - Spring Boot REST API

\- `db/` - Database dumps and Snowflake scripts

\- `docs/` - Architecture diagrams, notes



---



\## Setup



\### Backend

```bash

cd backend

mvn clean install

mvn spring-boot:run



