FROM postgres:13.2-alpine

ENV POSTGRES_DB=groceries-organizer
COPY plugins/src/main/resources/sql /docker-entrypoint-initdb.d/
