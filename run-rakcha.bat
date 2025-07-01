@echo off
setlocal EnableDelayedExpansion

:: Set the project directory
set PROJECT_DIR=%~dp0
cd %PROJECT_DIR%

:: Set Java system properties for StAX implementation
set JAVA_OPTS=-Djavax.xml.stream.XMLInputFactory=com.ctc.wstx.stax.WstxInputFactory ^
-Djavax.xml.stream.XMLOutputFactory=com.ctc.wstx.stax.WstxOutputFactory ^
-Djavax.xml.stream.XMLEventFactory=com.ctc.wstx.stax.WstxEventFactory ^
-Djakarta.xml.bind.JAXBContextFactory=org.glassfish.jaxb.runtime.v2.JAXBContextFactory

:: Run the application using Maven with dependency-list
mvn clean compile exec:java -Dexec.mainClass="com.esprit.MainApp" -Dexec.cleanupDaemonThreads=false ^
-Dexec.args="--add-opens java.base/java.lang=ALL-UNNAMED ^
--add-opens java.base/java.nio=ALL-UNNAMED ^
--add-opens java.base/java.util=ALL-UNNAMED ^
--add-exports java.base/sun.nio.ch=ALL-UNNAMED ^
--add-exports java.management/sun.management=ALL-UNNAMED ^
--add-exports jdk.unsupported/sun.misc=ALL-UNNAMED ^
--add-exports java.xml/com.sun.org.apache.xerces.internal.dom=ALL-UNNAMED ^
--add-opens java.xml/com.sun.org.apache.xerces.internal.util=ALL-UNNAMED ^
--add-opens java.xml/com.sun.org.apache.xerces.internal.parsers=ALL-UNNAMED ^
--add-opens java.xml/com.sun.org.apache.xerces.internal.impl=ALL-UNNAMED ^
--add-opens java.xml/com.sun.org.apache.xerces.internal.impl.dtd=ALL-UNNAMED ^
--add-opens java.xml/com.sun.org.apache.xerces.internal.impl.xs=ALL-UNNAMED ^
--add-opens java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED ^
--add-opens java.base/java.io=ALL-UNNAMED ^
--add-opens java.base/java.util.regex=ALL-UNNAMED"
