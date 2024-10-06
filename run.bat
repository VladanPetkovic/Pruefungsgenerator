@echo off

REM First: build the jar-file with: mvn clean package
REM then: you can double-click this bat-file
java --add-exports javafx.base/com.sun.javafx.event=org.controlsfx.controls -jar target\Pruefungsgenerator-1.0-SNAPSHOT-spring-boot.jar
pause
