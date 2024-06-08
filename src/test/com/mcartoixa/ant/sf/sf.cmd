@ECHO OFF
:: Simulates the Salesforce CLI by outputting a json file corresponding to the supplied command parameter.
:: For instance sf.cmd org list  will output the content of org\list.json

:: Reset ERRORLEVEL
VERIFY OTHER 2>nul
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION
IF ERRORLEVEL 1 GOTO ERROR_EXT

SET _RESULT_FILE_PATH=%~dp0
:ARGS_PARSE
IF "%~1" EQU "" GOTO END
SET _ARG=%~1
IF "%~1" EQU "import" SET _ARG=imp
IF "%~1" EQU "pkg" SET _ARG=pkg
SET _RESULT_FILE_PATH=%_RESULT_FILE_PATH%\%_ARG%
IF EXIST "%_RESULT_FILE_PATH%.json" GOTO ARGS_DONE
SHIFT
GOTO ARGS_PARSE
:ARGS_DONE

type "%_RESULT_FILE_PATH%.json"
GOTO END



:ERROR_EXT
ECHO [31mCould not activate command extensions[0m 1>&2
GOTO END

:END
ENDLOCAL
