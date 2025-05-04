@echo off
echo Killing any existing Java processes...
taskkill /F /IM java.exe 2>nul

echo Setting JAVA_HOME...
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.4.1

echo Cleaning and compiling the project...
call mvnw.cmd clean compile

echo Running the application...
call mvnw.cmd javafx:run

pause
