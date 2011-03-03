@echo off
@echo Running on Java:
java -version
@echo ...
@echo Cleaning all jAnrufmonitor non service files ...
java -Djava.library.path=. -cp jam.jar;jamapi.jar de.janrufmonitor.application.CleanExe >> logs/cleanexeresult.log
@echo ...finished !
