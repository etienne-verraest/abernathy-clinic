# Patients microservice
**Patients microservice is a microservice that manages patients from Abernathy Clinic.
From this service and its API specifications, you will be able to create, update, read and delete them.**

## Technical Stack
Microservice is built with the followings technologies :
- Java 11 and Spring Framework 2.7.5
- MySQL for database management system
- Maven for the application lifecycle management

Thanks to the Spring Framework, it is possible to use dependencies such as *Feign*, that acts as an intermediary for microservices to communicate

# Setting up the microservice
In order to store datas, you must create a MySQL database with the following steps with your terminal :
- Create a database named `abernathyclinic` : `CREATE DATABASE abernathyclinic;` (default credentials are root:root and can be changed in `application.properties`)
- Move to the new database using : `USE abernathyclinic;`
- Load the schema (found in `src/main/resources`) using : `SOURCE schema.sql;`

If you have followed the steps carefully, database is now created and your are able to do CRUD operations on Patients Microservice.

# API Specifications
**Microservice url is configured to serve datas on `localhost:9001/api/`**.

## **GET `/patients`**
This route gets the list of every patients registered in database

### **Successful response example**
**URL : `localhost:9001/api/patients`**
```json
[
    {
        "id": "ED88884",
        "firstName": "ETIENNE",
        "lastName": "DUPONT",
        "dateOfBirth": "1998-12-31",
        "gender": "M",
        "address": "123 rue des Pommiers",
        "phone": "789-101-1112"
    },
    {
        "id": "ED45951",
        "firstName": "ETIENNE",
        "lastName": "DUPONT",
        "dateOfBirth": "1968-12-31",
        "gender": "M",
        "address": "456 Rue des Cerisiers",
        "phone": "808-404-2020"
    },
    {
        "id": "RD11971",
        "firstName": "RENÉE",
        "lastName": "DUPONT",
        "dateOfBirth": "1938-12-31",
        "gender": "F",
        "address": "789 rue des Tulipes",
        "phone": "986-123-4444"
    }
]
```

## **GET `/patients?firstName=[firstName]&lastName=[lastName]`**
This route gets patients that are registered with the given first name and last name.

### **Successful response example**
**URL : `localhost:9001/api/patients?firstName=Etienne&lastName=Dupont`**
```json
[
    {
        "id": "ED88884",
        "firstName": "ETIENNE",
        "lastName": "DUPONT",
        "dateOfBirth": "1998-12-31",
        "gender": "M",
        "address": "123 rue des Pommiers",
        "phone": "789-101-1112"
    },
    {
        "id": "ED45951",
        "firstName": "ETIENNE",
        "lastName": "DUPONT",
        "dateOfBirth": "1968-12-31",
        "gender": "M",
        "address": "456 Rue des Cerisiers",
        "phone": "808-404-2020"
    }
]
```
### **Unsuccessful response example**
```json
{
    "time": "2022-11-15T17:43:24.868+00:00",
    "status": "NOT_FOUND",
    "message": "The patient with given first name and last name was not found"
}
```

## **GET `/patients/{id}`**
This route gets a patient by its ID. Only one patient will be returned from this route, if the patient exists.

### **Successful response example**
**URL : `localhost:9001/api/patients/RD11971/`**
```json
{
    "id": "RD11971",
    "firstName": "RENÉE",
    "lastName": "DUPONT",
    "dateOfBirth": "1938-12-31",
    "gender": "F",
    "address": "789 rue des Tulipes",
    "phone": "986-123-4444"
}
```

### **Unsuccessful response example**
This route returns null (This is voluntary and facilitates communication with other microservices).

## **POST `/patients/`**
This route creates a patient. Fields are validated and a custom error message will be returned if there are any. 

### **Successful content example**
**URL : `localhost:9001/api/patients/`**
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
**Note :** Gender takes either the value of M (for *"Male"*) or F (for *"Female"*).

### **400 Bad request on malformed content**
```json
{
    "time": "2022-11-15T17:44:11.668+00:00",
    "status": "BAD_REQUEST",
    "message": "The following fields are incorrect : phone"
}
```

## **PUT `/patients/{id}/`**
This route updates a patient given its ID. Fields are validated and a custom error message will be returned if there are any. The following JSON can be used in the body to send datas (the same that is used on POST route) :

### **Successful content example**
**URL : `localhost:9001/api/patients/RD11971/`**
```json
{
    "firstName": "RENÉE",
    "lastName": "DUPONT",
    "dateOfBirth": "1938-12-31",
    "gender": "F",
    "address": "789 rue des Tulipes, Boite Postale 2",
    "phone": "986-123-4444"
}
```

### **400 Bad Request on malformed content**
```json
{
    "time": "2022-11-15T17:45:57.779+00:00",
    "status": "BAD_REQUEST",
    "message": "The following fields are incorrect : phone"
}

```

## **DELETE `/patients/{id}`**
This route deletes a patient from database, given its ID. It also deletes attached notes from the notes microservice (if there are any).
A message will indicate if the operation is successful.

### **Successful response example**
**URL : `localhost:9001/api/patients/ED88884/`**
```json
Patient with id 'ED88884' was successfully deleted. Notes attached to him were also deleted.
```

### **404 Not found on wrong patient ID**
```json
{
    "time": "2022-11-15T17:46:48.108+00:00",
    "status": "NOT_FOUND",
    "message": "Patient with given ID was not found."
}
```