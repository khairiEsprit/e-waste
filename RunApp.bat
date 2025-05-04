@echo off
echo Killing all Java processes...
taskkill /F /IM java.exe 2>nul

echo Setting JAVA_HOME...
set "JAVA_HOME=C:\Program Files\Java\jdk-17.0.4.1"

echo Running the application...
cd /d "c:\Users\User\Documents\e-waste\e-waste"
call mvnw.cmd clean javafx:run

pause
