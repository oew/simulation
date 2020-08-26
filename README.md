#  JMX simulation 
captures existing JMX servers, creates a simulation, and recreates the captured server and applies the simulation to the server.

## Build Requires
- java 1.8 JDK (or greater)

- gradle 6.x
 
## To Build
- set java_home=...

- gradle customJar

## Runtime requires
- demo files (jar, shell scripts, txt files)

- java 1.8 JDK (or greater)

## To Capture a domain
-captureJmx.sh : a demo script captures a Catalina domain from JMX MBeanServer (localhost:9097) and stores it in the tomcat directory.

- java -cp simulation-full.jar -Dlog4j.configurationFile=./log4j2.xml com.tmm.capture.jmx.JmxSnapshot <ip> <port> <domain> <usernameOrDash> <PasswordOrDash> <OutputDir>
  
- java -cp simulation-full.jar -Dlog4j.configurationFile=./log4j2.xml com.tmm.capture.jmx.JmxSnapshot localhost 9097 "Catalina" - - "./tomcat/"

## To make a (default) simulation 
- makeSim.sh : a demo that takes an existing Catalina capture and creates a default simulation (auto.json). This is used to create a JSON file to modify.

- java -cp simulation-full.jar -Dlog4j.configurationFile=./log4j2.xml com.tmm.simulation.MapToSimulation <inputDir> <sourceDomain> <targetDomain> <outputFile>
 
 - OPTIONAL : Edit the output JSON file to modify the simulation.

## To Run a Simulation
- runSim.sh : a demo that runs a simulation from a captured Catalina domain.

- java -cp simulation-full.jar -Dlog4j.configurationFile=./log4j2.xml -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 

- Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false com.tmm.simulation.MapSimulation <simulationFile>
