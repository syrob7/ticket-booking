@echo off

call mvn clean package

java -jar .\target\ticket-booking-0.0.1-SNAPSHOT.jar
