# Notes microservice
**Notes microservice is a microservice that manages patients' notes from Abernathy Clinic.
From this service and its API specifications, you will be able to create, update, read and delete them.**

## Technical Stack
Microservice is built with the followings technologies :
- Java 11 and Spring Framework 2.7.5
- MongoDB for the database management system
- Maven for the application lifecycle management

Thanks to the Spring Framework, it is possible to use dependencies such as *Feign*, that acts as an intermediary for microservices to communicate

## API Specifications
**Microservice url is configured to serve datas on `localhost:9002`**.
Each url is prefixed with `api/`

### GET `/notes/{patientId}`
Get every notes related to a given patient (recognizable by its ID). Response is composed of multiple notes (see below for an example).

### GET `/note/{noteId}`
This route gets a single note, based on the note ID. Example of response could be :
```json
{
    "id": "636e5f27623af62b82e1a772",
    "date": "2022-11-11T14:41:43.592+00:00",
    "patientId": "LH76662",
    "content": "Patient feels tired. Blood check needed"
}
```

### POST `/notes/`
This route creates a note for a given patient. Patient must exist in order to add notes. The following JSON can be used in the body to send datas : 
```json
{
  "patientId": "LH76662",
  "content": "Patient feels tired. Blood check needed"
}
```
**Gender takes either the value of M (for "Male") or F (for "Female").**

### PUT `/notes/{noteId}`
This route updates a note based on its ID. Patient must exist in order to update the note, and the note must be linked to the given patientID. <br>
The following JSON can be used in the body to send datas (the same that is used on POST route) :

```json
{
  "patientId": "LH57530",
  "content": "Blood check required on an empty stomach"
}
```

### DELETE `/notes/{noteId}`
This route delete a single note based on its ID. If the operation is successful, a boolean set to true is returned.

### DELETE `/notes/all/{patientId}/`
This route deletes every notes included in the patient history. It is used primarily by the patients microservice when DELETE route is called.
If the operation is successful, a boolean set to true is returned.