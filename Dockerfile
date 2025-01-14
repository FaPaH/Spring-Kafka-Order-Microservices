FROM maven:3.8.5-openjdk-17 AS build
COPY core/pom.xml core/
COPY core/src core/src
RUN mvn -f core/pom.xml clean package install -Dmaven.test.skip=true

COPY order-service/pom.xml order-service/
COPY order-service/src order-service/src
RUN mvn -f order-service/pom.xml clean package install:install-file -Dfile=../core/target/core-0.0.1-SNAPSHOT.jar -DgroupId=com.fapah -DartifactId=core -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -Dmaven.test.skip=trueclean install -Dmaven.test.skip=true

#COPY stock-service/pom.xml stock-service/
#COPY stock-service/src stock-service/src
#RUN mvn -f stock-service/pom.xml clean package install:install-file -Dfile=../core/target/core-0.0.1-SNAPSHOT.jar -DgroupId=com.fapah -DartifactId=core -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -Dmaven.test.skip=trueclean install -Dmaven.test.skip=true

#FROM openjdk:17-jdk-slim
#COPY --from=build stock-service/target/*.jar root/stock-service.jar
#ENTRYPOINT ["java", "-jar", "root/stock-service.jar"]

FROM openjdk:17-jdk-slim
COPY --from=build order-service/target/*.jar root/order-service.jar
ENTRYPOINT ["java", "-jar", "root/order-service.jar"]