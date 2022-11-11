# Patients microservice
**Patients microservice is a microservice that manages patients from Abernathy Clinic.
From this service and its API specifications, you will be able to create, update, read and delete them.**

## Technical Stack
Microservice is built with the followings technologies :
- Java 11 and Spring Framework 2.7.5
- MySQL for database management system
- Maven for the application lifecycle management

Thanks to the Spring Framework, it is possible to use dependencies such as *Feign*, that acts as an intermediary for microservices to communicate

## API Specifications
**Microservice url is configured to serve datas on `localhost:9001`**.
Each url is prefixed with `api/`

### GET `/patients`
`/patients` can be called with two optional parameters : first name and last name

- `Without parameters` : Gets the list of every patients registered in database
- `/patients?firstName=[firstName]&lastName=[lastName]` : Gets patients that are registered with the given first name and last name.

### GET `/patients/{id}`
This route gets a patient by its ID. Only one patient will be returned from this route, if the patient exists.

### POST `/patients/`
This route creates a patient. Fields are validated and a custom error message will be returned if there are any. The following JSON can be used in the body to send datas :
```json
{
    "firstName": "firstName",
    "lastName": "lastName",
    "dateOfBirth": "YYYY-MM-DD",
    "gender": "M",
    "address": "Address",
    "phone": "XXX-XXX-XXXX"
}
```
**Gender takes either the value of M (for "Male") or F (for "Female").**

### PUT `/patients/{id}`
This route updates a patient given its ID. Fields are validated and a custom error message will be returned if there are any. The following JSON can be used in the body to send datas (the same that is used on POST route) :
```json
{
    "firstName": "firstName",
    "lastName": "lastName",
    "dateOfBirth": "YYYY-MM-DD",
    "gender": "M",
    "address": "Address",
    "phone": "XXX-XXX-XXXX"
}
```

### DELETE `/patients/{id}`
This route deletes a patient from database, given its ID. It also deletes attached notes from the notes microservice (if there are any).
A message will indicate if the operation is successful.