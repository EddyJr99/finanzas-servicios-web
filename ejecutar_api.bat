@echo off
rem Arranca la API REST con el Maven incluido en tools\ (no hay que instalar nada, solo Java 17).
rem La primera vez descarga las dependencias de Maven Central: requiere internet.
rem Uso: ejecutar_api.bat            (puerto 8080)
rem      ejecutar_api.bat 9090       (otro puerto)
cd /d "%~dp0"
if not "%~1"=="" set SERVER_PORT=%~1
echo Iniciando los servicios web del SGFP... espere el mensaje "Started FinanzasApiApplication".
echo Asegurese de que MySQL (Laragon) este iniciado y la base de datos sgfp exista.
call "%~dp0tools\apache-maven-3.9.11\bin\mvn.cmd" -q spring-boot:run
pause
