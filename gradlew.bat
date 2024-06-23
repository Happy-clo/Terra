@rem 关闭批处理脚本中的命令回显，如果没有定义DEBUG变量
@if "%DEBUG%"=="" @echo off

@rem ##########################################################################
@rem #
@rem #  Gradle启动脚本，用于Windows
@rem #
@rem ##########################################################################

@rem 在Windows NT环境下设置局部变量
@if "%OS%"=="Windows_NT" setlocal

@rem 获取脚本的目录路径
set DIRNAME=%~dp0
@rem 如果DIRNAME为空，则将其设置为当前目录
if "%DIRNAME%"=="" set DIRNAME=.
@rem 获取脚本的基础名称
set APP_BASE_NAME=%~n0
@rem 设置应用程序的主目录
set APP_HOME=%DIRNAME%

@rem 解析APP_HOME中的任何".""和".."，以简化路径
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem 在此处定义默认的JVM选项
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem 从JAVA_HOME环境变量中查找java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

@rem 尝试在PATH中找到java.exe
set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
@rem 如果找到了java.exe，则继续执行
if %ERRORLEVEL% equ 0 goto execute

@rem 如果找不到java.exe，则显示错误信息
echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
@rem 清除JAVA_HOME中的引号
set JAVA_HOME=%JAVA_HOME:"=%
@rem 设置java.exe的路径
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

@rem 检查JAVA_EXE是否存在
if exist "%JAVA_EXE%" goto execute

@rem 如果JAVA_EXE不存在，则显示错误信息
echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem 设置classpath
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

@rem 执行Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

@end
@rem 在Windows NT环境下结束局部变量
if "%OS%"=="Windows_NT" endlocal

:omega