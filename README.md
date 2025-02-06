# Bank Customer Statement Processor

## Overview
The bank receives monthly deliveries of customer statement records in CSV and XML formats. These records must be validated based on a simplified version of the MT940 format.

### **Input Format**
Each transaction record includes the following fields:

| Field | Description |
|--------|-------------|
| **Transaction Reference** | A numeric value |
| **Account Number** | An IBAN |
| **Start Balance** | The starting balance in Euros |
| **Mutation** | Either an addition (+) or a deduction (-) |
| **Description** | Free text |
| **End Balance** | The end balance in Euros |

### **Validation Rules**
1. All transaction references must be **unique**.
2. The **end balance** must match the calculated balance `(Start Balance + Mutation)`.

### **Output**
At the end of processing, a report will be generated displaying the **transaction reference** and **description** of any failed records.


# Solution
To solve this problem, I have chosen a Java Spring Boot application that exposes a REST API for uploading CSV or XML files. The API currently allows only one file to be uploaded at a time. After processing, the application generates a JSON report as the response to the POST request. 

The instructions to start using the app is added below. 




## 1 Build the Docker Image 

```sh
docker build -t bank-statement-processor .
```
The app compilation step is already happeing inside the docker image


## 2 Run the Application
```sh
docker run -p 8080:8080 bank-statement-processor
```

## 3 Use CURL to Upload Files
### Upload CSV File
```sh
curl -X POST -F "file=@path/to/transactions.csv" http://localhost:8080/api/v1/upload
```
Test csv files is included in the folder 'data' in this repo

### Upload XML File
```sh
curl -X POST -F "file=@path/to/transactions.xml" http://localhost:8080/api/v1/upload
```
Test xml files is included in the folder 'data' in this repo

### Sample output
```
curl -X POST -F "file=@records.csv" http://localhost:8080/api/v1/upload
[{"transactionRef":"112806","errorMessage":"Duplicate reference"},{"transactionRef":"112806","errorMessage":"Duplicate reference"}]
```

## Running Unit tests and funtional tests
Test files are added as part of the repo. Please ensure Apache maven is installed to run these tests. 
Run the tests using below command. 

```sh
mvn clean verify
```
