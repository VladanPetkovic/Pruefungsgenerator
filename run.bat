@echo off

REM First: build the jar-file with: mvn clean package
REM then: you can double-click this bat-file
java -jar target\Pruefungsgenerator-1.0-SNAPSHOT-spring-boot.jar
pause
