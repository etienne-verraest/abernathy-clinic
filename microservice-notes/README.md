# Notes microservice
**Notes microservice is a microservice that manages patients' notes from Abernathy Clinic.
From this service and its API specifications, you will be able to create, update, read and delete them.**

## Technical Stack
Microservice is built with the followings technologies :
- Java 11 and Spring Framework 2.7.5
- MongoDB for the database management system
- Maven for the application lifecycle management

Thanks to the Spring Framework, it is possible to use dependencies such as *Feign*, that acts as an intermediary for microservices to communicate

# Setting up the microservice
To make the microservice working, you must follow the following steps :
- Create a MongoDB database named `abernathyclinic`
- In the new MongoDB database, create a collection named : `notes`

That's all ! You can now add notes to patient microservice.

# API Specifications
**Microservice url is configured to serve datas on `localhost:9002/api/`**. <br>
In order to function, patient microservice must be started.

## **GET `/notes/{patientId}`**
Get every notes related to a given patient (recognizable by its ID). Response is composed of multiple notes.

### **Successful response example**
**URL : `localhost:9002/api/notes/RD11971/`**
```json
[
    {
        "id": "6372910a44d5562320acad2e",
        "date": "2022-11-14T19:03:38.096+00:00",
        "patientId": "RD11971",
        "content": "Patient has gained 10 lbs in 1 month"
    },
    {
        "id": "637290f244d5562320acad2d",
        "date": "2022-11-14T19:03:14.847+00:00",
        "patientId": "RD11971",
        "content": "Patient states that she is a smoker"
    }
]
```

### **404 Not Found**
```json
{
    "time": "2022-11-15T17:54:39.082+00:00",
    "status": "NOT_FOUND",
    "message": "Patient with given ID was not found"
}
```

## **GET `/note/{noteId}`**
This route gets a single note, based on the note ID.

### **Successful response example**
**URL : `localhost:9002/api/note/6372910a44d5562320acad2e`**
```json
{
    "id": "6372910a44d5562320acad2e",
    "date": "2022-11-14T19:03:38.096+00:00",
    "patientId": "RD11971",
    "content": "Patient has gained 10 lbs in 1 month"
}
```

### **Unsuccessful response example**
This route returns null (This is voluntary and facilitates communication with other microservices).

## **POST `/notes/`**
This route creates a note for a given patient. Patient must exist in order to add notes.

### **Successful content example**
**URL : `localhost:9002/api/notes`**
```json
{
  "patientId": "RD11971",
  "content": "Patient states that she is a smoker"
}
```

### **400 Bad Request**
```json
{
    "time": "2022-11-15T17:56:33.676+00:00",
    "status": "BAD_REQUEST",
    "message": "The following fields are incorrect : content"
}
```

### **404 Not Found**
```json
{
    "time": "2022-11-15T17:57:02.045+00:00",
    "status": "NOT_FOUND",
    "message": "Patient with given ID was not found"
}
```

## **PUT `/notes/{noteId}`**
This route updates a note based on its ID. Patient must exist in order to update the note, and the note must be linked to the given patientID. <br>

### **Successful content example**
**URL : `localhost:9002/api/notes/6372910a44d5562320acad2e`**
```json
{
  "patientId": "RD11971",
  "content": "Patient has gained too much weight in a week"
}
```

### **400 Bad Request**
```json
{
    "time": "2022-11-15T17:56:33.676+00:00",
    "status": "BAD_REQUEST",
    "message": "The following fields are incorrect : content"
}
```

### **404 Not Found**
```json
{
    "time": "2022-11-15T17:57:02.045+00:00",
    "status": "NOT_FOUND",
    "message": "Patient with given ID was not found"
}
```

## **DELETE `/notes/{noteId}`**
This route delete a single note based on its ID. If the operation is successful, a boolean set to true is returned.

### **Successful response example**
**URL : `localhost:9002/api/notes/637290f244d5562320acad2d`**
```json
true
```

### **404 Not Found**
```json
{
    "time": "2022-11-15T17:59:38.803+00:00",
    "status": "NOT_FOUND",
    "message": "Note with given ObjectID was not found"
}
```

## **DELETE `/notes/all/{patientId}/`**
This route deletes every notes included in the patient history. It is used primarily by the patients microservice when DELETE route is called.
If the operation is successful, a boolean set to true is returned.

### **Successful response example**
**URL : `localhost:9002/api/notes/all/RD11971`**
```json
true
```

### **404 Not Found**
```json
{
    "time": "2022-11-15T18:01:32.798+00:00",
    "status": "NOT_FOUND",
    "message": "Patient with given ID was not found"
}
```