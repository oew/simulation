java -cp simulation-full.jar -Dlog4j.configurationFile=./log4j2.xml -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -javaagent:/home/everett/dev/util/prometheus/jmx_prometheus_javaagent-0.13.0.jar=localhost:8081:config.yaml com.ew.simulation.MapSimulation "./tomcat/auto.json" 