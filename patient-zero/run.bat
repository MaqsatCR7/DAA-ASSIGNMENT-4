@echo off
set JAVA_HOME=C:\Users\maksa\.jdks\openjdk-21.0.2
set PATH=%JAVA_HOME%\bin;%PATH%
if exist out rmdir /s /q out
javac -encoding UTF-8 -d out src\*.java
java -cp out Main
echo.
echo Gotovo. Otkroy output\outbreak.html v brauzere.
pause
