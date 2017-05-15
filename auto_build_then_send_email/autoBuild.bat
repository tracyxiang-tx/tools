@echo off

set prod=%1
set branch=%2

set GitHubPath=%3
set toList=%4
set ccList=%5

set toolDIR=F:\tracy\autoBuild

set logDIR=%toolDIR%\log

set ProdPath=%GitHubPath%\%prod%

set version=%branch:~4,9%
set acaVersion=%branch:~4,1%
set acalog=%ProdPath%\msbuild1.log

if not exist %logDIR% mkdir %logDIR%
if %prod% EQU AA_Prod set resultLog=%logDIR%\aa.log
if %prod% EQU ACA_Prod set resultLog=%logDIR%\aca.log

cd/d %ProdPath%
git clean -xdf
git checkout .
git pull
git checkout %branch%

if %prod% EQU AA_Prod (

echo cd/d %toolDIR% >> av-env.cmd 
echo call clean.bat %ProdPath%  >> av-env.cmd 
echo cd/d %toolDIR% >> av-env.cmd 
echo call build.bat %resultLog% %ProdPath% >> av-env.cmd 
echo exit >> av-env.cmd  
start /wait av-env.cmd )

if %prod% EQU ACA_Prod (
setlocal enabledelayedexpansion
set src=writeversion.bat %%1

(for /f "delims=" %%i in (Build.bat) do (
    	
	echo %%i| Find /i "writeversion.bat" >nul
	If !errorLevel!==0 (
	echo call writeversion.bat %%1
	) Else (echo %%i)
	
)) > %toolDIR%\acaTemp.bat

(for /f "delims=" %%i in (%toolDIR%\acaTemp.bat) do (
    	
	echo %%i| Find /i "pause" >nul
	If !errorLevel!==0 (echo exit
	) Else (echo %%i)
	 
)) > build.bat

if %acaVersion% EQU 7 echo :end >> build.bat
echo exit >> build.bat
del %toolDIR%\acaTemp.bat

start /wait Build.bat %version%
echo nul > %resultLog%
xcopy /y %acalog% %resultLog%
)
pause

if %prod% EQU AA_Prod type  %resultLog% | find /C "BUILD SUCCESSFUL" > nul
if %prod% EQU ACA_Prod type %resultLog% | find /C "Build succeeded"  > nul
if %errorlevel%==0 (
	echo         == Have send email: %time%
	cd/d %toolDIR%
	java -jar sendEmail.jar %prod% %branch% success %resultLog% %toList% %ccList%
) else ( 
	echo         == Failed: Unable to compile source code!
	cd/d %toolDIR%
	java -jar sendEmail.jar %prod% %branch% faild %resultLog% %toList% %ccList%
)

exit