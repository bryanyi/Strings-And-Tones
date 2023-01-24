# Strings and Tones Microservice Notes

## Creating a Microservice
`mvn archetype:generate -DgroupId=com.stringsandtones -DartifactId=ProductService -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false`

## Seting up a postgres database


## Setting up Eureka Server
1. Create a new spring initializr with spring-cloud-starter-netflix-eureka-server dependency.
2. In the microservice, add dependency called spring-cloud-starter-netflix-eureka-client
3. IMPORTANT: in pom.xml, the dependencyManagement needs to be above dependencies
  - Inside of dependencyManagement, have these two:
  - ```
    <dependencies>
          <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring.boot.dependencies.version}</version>
            <scope>import</scope>
            <type>pom</type>
          </dependency>
          <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
          </dependency>
        </dependencies>

      ```
## Handling repetitive configurations
- Creating a global config via `spring-cloud-config-server` and `spring-cloud-starter-config`.

## How does one service call another service's API? We use Feign Client
- Need to figure out how to create libraries so that external clients can be called from a separate package rather than repeating reused classes.

## Installing openzipki
- `docker run -d -p 9411:9411 openzipkin/zipkin`
- Starting Spring Boot version 3.0, Sleuth has changed to micrometer.
- I switched to version 2.7.3 to implement Sleuth. Had to clear and restart cache on Intellij.

## Building the Order Service's response.
- Using RestTemplate to call external API's. 
- I have to get the same class model's from the other microservice's, so there are common classes between different microservices. 
  - I'm sure I have to somehow put these classes into an external microservice to avoid repeating classes. Not sure how to achieve that right now.


## Frustrations
- Creating a new project but keeping all the dependencies consistent.
  - Is there an alternative to start.spring.io besides utilizing the command line to apply dependencies as well as versioning?

## Implementing a circuit breaker in the api gateway
- Link to docs: https://resilience4j.readme.io/docs/circuitbreaker

