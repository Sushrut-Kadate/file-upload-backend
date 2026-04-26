# File Upload Backend

A production-ready file upload system built with Spring Boot, MySQL, and Cloudinary.

## Tech Stack

- **Backend** - Spring Boot
- **Database** - MySQL
- **Cloud Storage** - Cloudinary
- **Build Tool** - Maven

## Features

- Upload single or multiple files
- Files stored on Cloudinary cloud storage
- File metadata stored in MySQL database
- Delete files from both Cloudinary and database
- REST API with proper error handling

## Setup & Installation

**1. Clone the repository**
```bash
git clone https://github.com/Sushrut-Kadate/file-upload-backend.git
```

**2. Create MySQL database**
```sql
CREATE DATABASE file_demo;
```

**3. Configure application properties**

Copy `application.properties.example` to `application.properties` and fill in your details:
```properties
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
```

**4. Run the application**
```bash
./mvnw spring-boot:run
```

Application runs at `http://localhost:8080`

## Frontend

React + Vite frontend — [file-upload-frontend](https://github.com/Sushrut-Kadate/file-upload-frontend)

## Acknowledgement

Inspired by [callicoder's spring-boot-file-upload-download-rest-api-example](https://github.com/callicoder/spring-boot-file-upload-download-rest-api-example)