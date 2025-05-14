@echo off
echo Listing all Java processes...
tasklist /FI "IMAGENAME eq java.exe"

echo.
set /p confirm=Do you want to kill all Java processes? (Y/N): 

if /i "%confirm%"=="Y" (
    echo Killing all Java processes...
    taskkill /F /IM java.exe
    echo All Java processes have been terminated.
) else (
    echo Operation cancelled.
)

pause
