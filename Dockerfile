FROM ghcr.io/navikt/baseimages/temurin:17

COPY build/libs/pensjon-opptjening-afp-api.jar /app/app.jar

ENV JAVA_OPTS=-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/
