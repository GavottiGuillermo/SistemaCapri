@echo off
REM CapriStoreLauncher.bat
REM Ejecuta el script PowerShell en modo oculto, sin mostrar ventana de consola

setlocal
set SCRIPT_PATH=%~dp0CapriStoreLauncher.ps1

REM Ejecutar PowerShell en modo oculto
powershell.exe -WindowStyle Hidden -ExecutionPolicy Bypass -File "%SCRIPT_PATH%"
endlocal

