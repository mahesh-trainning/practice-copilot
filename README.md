# Practice Copilot API

Java REST API project built with JAX-RS (Jersey) + Grizzly + Maven.

## Tech Stack

- Java 17
- Jersey 3.x (JAX-RS)
- Grizzly HTTP server
- Jackson (JSON) + Java Time module (`jackson-datatype-jsr310`)
- Maven
- CSV-based mock persistence for customers

## Project Structure

```text
practice-copilot/
	docs/
		customer-api-openapi.yaml
		customer-api-postman-collection.json
	src/
		main/
			java/com/mahesh/practicecopilot/api/
				bootstrap/
					Main.java
				config/
					ApiApplicationConfig.java
					ObjectMapperContextResolver.java
				models/
					CustomerResourceBean.java
					CustomerResourceRequest.java
					CustomerResourceResponse.java
					HealthResponse.java
				resources/
					CustomerResource.java
					HealthResource.java
				store/
					CustomerStore.java
			resources/
				application.properties
				customers.csv
		test/
			java/com/mahesh/practicecopilot/api/resources/
				HealthResourceTest.java
	pom.xml
```

## Build And Run

```bash
mvn clean compile
mvn exec:java
```

Server base URL:

- `http://localhost:8080/api`

## APIs

### Health

- `GET /api/health`

Response:

```json
{
	"status": "UP",
	"service": "practice-copilot-api"
}
```

### Customer CRUD

- `GET /api/customers/` - get all customers
- `GET /api/customers/{id}` - get a customer by id
- `POST /api/customers/` - create customer
- `PUT /api/customers/{id}` - update customer
- `DELETE /api/customers/{id}` - delete customer

Request body for create/update:

```json
{
	"customerResourceBean": {
		"id": 101,
		"name": "John Doe",
		"email": "john.doe@example.com",
		"phone": "9876543210",
		"address": "Bangalore"
	}
}
```

## Validation Rules

- `id`: mandatory
- `name`: mandatory
- `email`: optional; if provided must be valid email format
- `phone`: optional; if provided must be exactly 10 digits (`^[0-9]{10}$`)
- `address`: optional
- For update, request `id` must match path `id`

## createdDate Behavior

- `createdDate` is server-managed.
- Request `createdDate` is ignored.
- On create, server sets current date-time.
- On update, original stored `createdDate` is preserved (never overwritten to null).

## Persistence (Mock)

- Customer data is stored in `src/main/resources/customers.csv`.
- API reads/writes CSV through `CustomerStore`.

## API Documentation

- OpenAPI YAML: `docs/customer-api-openapi.yaml`
- Postman Collection JSON: `docs/customer-api-postman-collection.json`

Import Postman collection from the JSON file and use:

- `baseUrl = http://localhost:8080/api`

## Run Tests

```bash
mvn test
```
