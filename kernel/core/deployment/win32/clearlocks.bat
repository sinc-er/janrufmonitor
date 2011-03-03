@echo off
@echo Running on Java:
java -version
@echo ...
@echo Cleaning all jAnrufmonitor locks ...
java -Djava.library.path=. -cp jam.jar;jamapi.jar de.janrufmonitor.application.CleanLocks >> logs/cleanresult.log
@echo ...finished !
pause