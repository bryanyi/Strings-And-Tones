# Strings and Tones Microservice

## About This Project
The goal of this project is to develop a working knowledge of building basic microservices using SpringBoot and Docker.
This project is the backend of an eCommerce store that sells guitars and effects pedals, which is built with a microservice architecture.

As a current front-end developer, I wanted to focus more on the intricacies of microservices and the nuiances of Java/SpringBoot since this is my first backend project, and also my first time doing a project with Java.

Everything below this note are my notes as I followed multiple resources (Youtube, blogs, articles, etc) to build a working microservice backend.

## Creating a Microservice
`mvn archetype:generate -DgroupId=com.stringsandtones -DartifactId=ProductService -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false`


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

## Okta authentication
- After creating the application, I had to add people to the APPLICATION. 
- It wasn't enough just to have users set up inside of Okta's general dashboard.

## Dockerizing
- `docker pull redis`: pull down an image.
- `docker images`: see your images.
- `docker run --name redis-latest redis`: run the redis image.
- `docker ps -a`: See the available containers

## Creating our own docker images for each service with docker files
- Run `mvn clean install`
- Command to build the image for serviceregistry: `docker build -t bryanyidev/serviceregistry:0.0.1 .`
- Run the image I just created: `docker run -d -p8761:8761 --name serviceregistry f040a01130c4`
  - The last random values is the imageID that I can find when I run `docker images`
- Run the container with environment variables: `docker run -d -p8761:8761 -e CONFIG_SERVER_URL=host.docker.internal -e EUREKA_SERVER_ADDRESS=http://host.docker.internal:8761/eureka --name serviceregistry 7fc66ea48f83`
- See the container that was built: `docker ps`

## Publish the images I created
- First, log into docker with `docker login`
- Then push each image with `docker push bryanyidev/orderservice:latest

## Docker compose
- One file handy to execute all the files

## Maven JIB Plugin
- A maven plugin for building docker and oci images Java apps
- This plugin will automatically create the docker images and also pushing it to your account
- You first have to go into Intellij's maven settings to log into docker since it pushes the images to the account.
- After adding it to pom.xml, run this command: `mvn clean install jib:build`

## minikube kubectl
- `kubectl create deployment nginx --image=nginx`: Create a deployment using the nginx image.
- `kubectl get pods`: See the running pods.
- `kubectl logs {name of pod from get pods command}`: See the logs of the pod.
- `kubectl exec -it {name of pod} -- /bin/bash`: Go into pod.
- `kubectl edit deployment {name of deployment}`: Edit the deployment, which will show a yaml file

## Notes of a K8s YAML configuration
- There are three main parts to the yaml file - 1. metadata, 2. specification, 3. status.
  - The status portion is where k8s will compare the yaml file with the current status to keep it consistent
  - etcd will hold the current status of a k8s component
- Layers of abstraction:
  1. Deployment
    - Under spec, you can have template.metadata.
    - This metadata is what is applied to a pod.
    - Essentially, it's metadata within metadata.
    - template.spec will hold the blueprint of a pod such as its image and port
  2. RepicaSet
  3. Pod
- Connections are established via Deployment and Service
  1. A service yaml file will have a `spec.selector.app: nginx` field. This is the key field
  2. This will match with the Deployment yaml file's `metadata.labels.app: nginx` field as well as it's `spec.temaplte.metadata.labels.app: nginx`
  3. Service yaml is important for the pods to talk to each other.
- Ingress is used to accept external calls, which then forwards the call to the correct pods.

## K8s Namespace
- `kubectl create namespace stringsandtones`: This command will create a k8s namespace.
- `kubectl get namespace`: See the namespaces running
- Anything created will go into the default namespace if a specific namespace isn't defined.
- kube-system is a namespace that holds all k8s resources
- kube-public is a namespace pertaining to public accessibility.
- kube-node-lease is namespace to get lease information which is useful for healthchecks.
- Applying to a specific namespace with -n: `kubectl apply -f ./deploy.yaml -n stringsandtones`
