# content-service
The content-service for uploading an etalon pair to AWS S3.

## Project Status
[![Build Status](https://travis-ci.com/speech4j/content-service.svg?branch=master)](https://travis-ci.com/speech4j/content-service)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=speech4j_content-service&metric=alert_status)](https://sonarcloud.io/dashboard?id=speech4j_content-service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=speech4j_content-service&metric=coverage)](https://sonarcloud.io/dashboard?id=speech4j_content-service)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=speech4j_content-service&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=speech4j_content-service)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=speech4j_content-service&metric=sqale_index)](https://sonarcloud.io/dashboard?id=speech4j_content-service)

## Requirements
* Java 11
* Docker

## Building Instructions
 * `./gradlew clean build` -- build the project

## Testing Instructions
(If running from windows - replace / to \ )
 * `./gradlew clean test` -- build and run the tests.
 
## Launch Instructions 
 - terminal:
 * `java -jar build/libs/*.jar` -- run the project
 - docker:
 * `docker build .` - build docker image
 * `docker run image-name` - run app
 
## Running DB Instructions
 * create and run db container locally
 ```
 docker run --name postgres-docker \
                 -e POSTGRES_PASSWORD=postgres \
                 -e POSTGRES_USERNAME=postgres \
                 -e POSTGRES_DB=tenant_db \
                 -p 5432:5432 -d postgres
 ```
## Running DB Migration
* `./gradlew diffChangeLog` - generate a file with differences between the current db schema and persist entities
* `make migration MIGRATION_LABEL="log-version"` - generate diff file and include it in changelog-master.yaml