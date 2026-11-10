FlexyGames Server Setup
=======================

Java & Gradle Configuration:
----------------------------
- Java 8 path: C:/Program Files/Java/jdk8/jdk8u504-b01
- Gradle wrapper: gradlew (uses Java 8 via JAVA_HOME override)
- Always export JAVA_HOME before running gradlew commands

Server Startup Command:
-----------------------
export JAVA_HOME="C:/Program Files/Java/jdk8/jdk8u504-b01"
cd "D:/git-projects/flexygames"
gradlew bootRun

Data Seeding:
-------------
The seeder script is located at:
D:/git-projects/flexygames/grails-app/init/flexygames/DeveloperDataSeeder.groovy

This seeder is called automatically from Grails BootStrap.groovy on startup.
It creates:
- Test users (Mario, Giulla, Luca)
- Test teams (Test Team 1, Test Team 2)
- Test game types and actions
- Sample sessions

Endpoint Verification:
----------------------
After startup, verify endpoints:
- curl http://localhost:8080/
- curl http://localhost:8080/sessions/list
- curl http://localhost:8080/player/list
- curl http://localhost:8080/teams/list
