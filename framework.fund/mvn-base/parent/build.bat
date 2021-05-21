
@echo off
call ..\maven-env.bat

call %MVN% clean install -Dmaven.test.skip=true | %TEE% build.log
rem call %MVN% clean install -f third-party-libs\pom.xml -Dmaven.test.skip=true | %TEE% build.log
rem call %MVN% clean install -f uep-provided-libs\pom.xml -Dmaven.test.skip=true | %TEE% build.log

