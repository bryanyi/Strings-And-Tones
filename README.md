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
