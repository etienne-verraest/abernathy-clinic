# Notes microservice
**Notes microservice is a microservice that manages patients' notes from Abernathy Clinic.
From this service and its API specifications, you will be able to create, update, read and delete them.**

## Technical Stack
Microservice is built with the followings technologies :
- Java 11 and Spring Framework 2.7.5
- MongoDB for the database management system
- Maven for the application lifecycle management

Thanks to the Spring Framework, it is possible to use dependencies such as *Feign*, that acts as an intermediary for microservices to communicate

# API Specifications
**Microservice url is configured to serve datas on `localhost:9002/api/`**. <br>
In order to function, patient microservice must be started.

## GET `/notes/{patientId}`
Get every notes related to a given patient (recognizable by its ID). Response is composed of multiple notes.

### Response example
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

## GET `/note/{noteId}`
This route gets a single note, based on the note ID.

### Response example
**URL : `localhost:9002/api/note/6372910a44d5562320acad2e`**
```json
{
    "id": "6372910a44d5562320acad2e",
    "date": "2022-11-14T19:03:38.096+00:00",
    "patientId": "RD11971",
    "content": "Patient has gained 10 lbs in 1 month"
}
```

## POST `/notes/`
This route creates a note for a given patient. Patient must exist in order to add notes.

### Content example
**URL : `localhost:9002/api/notes`**
```json
{
  "patientId": "RD11971",
  "content": "Patient states that she is a smoker"
}
```

## PUT `/notes/{noteId}`
This route updates a note based on its ID. Patient must exist in order to update the note, and the note must be linked to the given patientID. <br>

### Content example
**URL : `localhost:9002/api/notes/6372910a44d5562320acad2e`**
```json
{
  "patientId": "RD11971",
  "content": "Patient has gained 12 lbs in 1 month"
}
```

## DELETE `/notes/{noteId}`
This route delete a single note based on its ID. If the operation is successful, a boolean set to true is returned.

### Response example
**URL : `localhost:9002/api/notes/637290f244d5562320acad2d`**
```json
true
```

## DELETE `/notes/all/{patientId}/`
This route deletes every notes included in the patient history. It is used primarily by the patients microservice when DELETE route is called.
If the operation is successful, a boolean set to true is returned.

### Response example
**URL : `localhost:9002/api/notes/all/RD11971`**
```json
true
```