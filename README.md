# Library Application

## Description
This application gives the below functionalities
1. Add Books
2. Borrow Books
3. Return Books
4. Add User
5. View Books
6. View Available books

## Requirements

For building and running the application you need:

- [JDK 1.11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven 3.6.3](https://maven.apache.org)

## Running the application locally

To run the Library application on your local machine, execute the `main` method in the `LibraryApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

Docker can also be used to run the application 
```shell
docker run -p="8000:80" library-app
```


##Swagger
API documentation is available at http://localhost:8080/swagger-ui.html#



