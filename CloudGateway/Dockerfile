FROM openjdk:17

# After running `mvn clean install`, a jar file will be available. Store that in a variable
ARG JAR_FILE=target/*.jar

# Renaming the jar file
COPY ${JAR_FILE} cloudgateway.jar

# Execution for when the container begins
# It's like `CMD`, but ENTRYPOINT is for when the container first spins up
ENTRYPOINT ["java", "-jar", "/cloudgateway.jar"]

# Same as its application.yml
EXPOSE 9090