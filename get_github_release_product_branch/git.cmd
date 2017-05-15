@ECHO off
SET /P preVersion="please enter previous version: "%1 
SET /P currentVersion="please enter release version: "%2 
java -jar git.jar %preVersion% %currentVersion%
pause