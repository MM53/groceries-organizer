FROM postgres:13.2-alpine

ENV POSTGRES_DB=groceries-organizer
COPY sql /docker-entrypoint-initdb.d/
