mkdir tomcat
java  -cp simulation-full.jar -Dlog4j.configurationFile=./log4j2.xml com.ew.capture.jmx.JmxSnapshot localhost  9097 "Catalina" - - "./tomcat/"

