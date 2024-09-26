FROM openjdk:17               
ADD partient-service-HSBC-0.0.1-SNAPSHOT.war partient-service-HSBC-0.0.1-SNAPSHOT.war 
ENTRYPOINT ["java","-jar","partient-service-HSBC-0.0.1-SNAPSHOT.war"]   
EXPOSE 8000  