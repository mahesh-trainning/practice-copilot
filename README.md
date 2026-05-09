# Practice Copilot - Java JAX-RS REST API

This workspace is now structured as a proper Java REST API project using JAX-RS (Jersey) and Maven.

## Folder Structure

```text
practice-copilot/
	pom.xml
	src/
		main/
			java/com/mahesh/practicecopilot/api/
				bootstrap/
					Main.java
				config/
					ApiApplicationConfig.java
				models/
					HealthResponse.java
				resources/
					HealthResource.java
			resources/
				application.properties
		test/
			java/com/mahesh/practicecopilot/api/resources/
				HealthResourceTest.java
```

## Required Libraries Added

- JAX-RS runtime: `org.glassfish.jersey.core:jersey-server`
- HTTP container: `org.glassfish.jersey.containers:jersey-container-grizzly2-http`
- Dependency injection: `org.glassfish.jersey.inject:jersey-hk2`
- JSON support: `org.glassfish.jersey.media:jersey-media-json-jackson`
- Logging: `org.slf4j:slf4j-simple`
- Testing: `org.junit.jupiter:junit-jupiter`
- JAX-RS testing: `org.glassfish.jersey.test-framework.providers:jersey-test-framework-provider-grizzly2`

## Run The API

```bash
mvn clean compile
mvn exec:java
```

The API starts at:

- `http://localhost:8080/api/health`

## Run Tests

```bash
mvn test
```
practice with copilot
