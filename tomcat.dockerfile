FROM tomcat:9.0.46-jdk16-openjdk-slim-buster

COPY plugins/target/plugins-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war