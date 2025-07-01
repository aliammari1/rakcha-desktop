@echo off
setlocal EnableDelayedExpansion

:: Set the project directory
set PROJECT_DIR=%~dp0
cd %PROJECT_DIR%

echo Testing XML configuration...

:: Run a simple test to verify StAX and JAXB
mvn exec:java -Dexec.mainClass="com.esprit.Config" -Dexec.args="--xml-test" ^
-Djavax.xml.stream.XMLInputFactory=com.ctc.wstx.stax.WstxInputFactory ^
-Djavax.xml.stream.XMLOutputFactory=com.ctc.wstx.stax.WstxOutputFactory ^
-Djavax.xml.stream.XMLEventFactory=com.ctc.wstx.stax.WstxEventFactory ^
-Djakarta.xml.bind.JAXBContextFactory=org.glassfish.jaxb.runtime.v2.JAXBContextFactory

pause
