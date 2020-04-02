# content-service
The content-service for uploading an etalon pair to AWS S3.

## Project Status
[![Build Status](https://travis-ci.com/speech4j/content-service.svg?branch=master)](https://travis-ci.com/speech4j/content-service)

## Requirements
* Java 11
* Docker

## Building Instructions
 * `./gradlew clean build` -- build the project

## Testing Instructions
(If running from windows - replace / to \ )
 * `./gradlew clean test` -- build and run the tests.
 
## Launch Instructions 
 - ternimal:
 * `java -jar build/libs/*.jar` -- run the project
 - docker:
 * `docker build .` - build docker image
 *  `docker run image` - run app
 
 
