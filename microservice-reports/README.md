# Reports microservice
**Reports microservice is a microservice that generate diabete risk report.
The microservice fetchs information from patient & notes microservice in order to generate it.**

## Technical Stack
Microservice is built with the followings technologies :
- Java 11 and Spring Framework 2.7.5
- Maven for the application lifecycle management

Thanks to the Spring Framework, it is possible to use dependencies such as *Feign*, that acts as an intermediary for microservices to communicate

# API Specifications
**Microservice url is configured to serve datas on `localhost:9003/api/`**.

## GET `/reports/generate/{patientId}`
This is the only route and it returns datas to assemble the report with complete sentences.

### Response example
**URL : `localhost:9003/api/reports/generate/RD11971`**
```json
{
    "patientId": "RD11971",
    "firstName": "RENÉE",
    "lastName": "DUPONT",
    "gender": "F",
    "birthdate": "1938-12-31",
    "age": 83,
    "risk": "Borderline",
    "triggers": [
        "smoker",
        "weight"
    ]
}
```

