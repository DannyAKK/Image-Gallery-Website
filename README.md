<img width="2160" height="1080" alt="image" src="https://github.com/user-attachments/assets/c8361b85-3432-42ab-a4b3-16cc55ff80af" />




# Image Gallery Web Application

A Spring Boot web application that allows registered users to create photo galleries and upload images. Built as part of a placement technical test while at the at the University of Bradford for the company, 'The Curve'.

---

## Features

- User registration and login with secure BCrypt password hashing
- Public gallery browsing so any visitor can view photo galleries
- Registered users can create, edit and delete their own galleries
- Registered users can upload, edit and delete their own photos
- Gallery and photo ownership rules so only the owner can manage their own content
- Only the gallery owner can add, edit or delete photos within that gallery
- Thumbnail gallery view for uploaded photos
- Click thumbnails to view full-size images in a modal overlay
- Automatic thumbnail generation on upload (300x300, aspect ratio preserved)
- Slideshow view with automatic 3-second transitions and manual previous/next controls
- Custom error pages for 404, 403 and 500 errors
- Unit testing with JUnit 5 and Maven Wrapper

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 21 | Core language |
| Spring Boot 3.5 | Application framework |
| Spring Security | Authentication and authorisation |
| Spring Data JPA | Database access layer |
| Thymeleaf | Server-side HTML templating |
| H2 (in-memory) | Database |
| Thumbnailator 0.4.20 | Thumbnail generation |
| JUnit 5 + Mockito | Unit testing |
| Maven | Build and dependency management |
| Docker | Containerisation |
| Docker Compose | Application packaging and startup |

---

## Requirements

- Docker Desktop

No separate Java or Maven installation is required when running with Docker, because the application is packaged inside a container and started through Docker Compose.

---

## How to Run with Docker

1. Clone the repository:
   ```bash
   git clone https://github.com/DannyAKK/Image-Gallery-Website.git
   ```

2. Navigate into the project folder:
   ```bash
   cd Image-Gallery-Website
   ```

3. Build and start the application:
   ```bash
   docker compose up --build
   ```

4. Open your browser and go to:
   ```text
   Home/Login page: http://localhost:8080
   Public Galleries page: http://localhost:8080/galleries/public
   ```

5. To stop the application:
   ```bash
   docker compose down or click 'CTRL & C'
   ```

---

## Run in Background

To run the application in detached mode:

```bash
docker compose up --build -d
```

To view logs:

```bash
docker compose logs -f
```

To stop the containers:

```bash
docker compose down
```

---

## Running Without Docker

If you want to run the application directly with Maven instead of Docker, use:

### Windows
```bash
.\mvnw.cmd spring-boot:run
```

### macOS/Linux
```bash
./mvnw spring-boot:run
```

This method requires Java 21 to be installed locally.

---

## Running Tests

### Windows
```bash
.\mvnw.cmd test
```

### macOS/Linux
```bash
./mvnw test
```

Tests are run using JUnit 5 through the Maven Wrapper.

---

## Project Structure

```text
src/
  main/
    java/uk/ac/bradford/imagegalleryweb/
      config/         - Web MVC configuration
      controller/     - HTTP request handlers
      entity/         - JPA database models
      repository/     - Spring Data JPA repository interfaces
      security/       - Spring Security configuration
      service/        - Business logic
    resources/
      templates/      - Thymeleaf HTML templates
        error/        - Custom error pages (404, 403, 500)
  test/
    java/             - Unit tests

uploads/              - Uploaded images stored here at runtime
Dockerfile            - Docker image definition
compose.yaml          - Docker Compose configuration
```

---

## H2 Database Console

The H2 in memory database console is available at:

```text
http://localhost:8080/h2-console
```

Use the following connection settings:

- **JDBC URL**: `jdbc:h2:mem:imagegallery`
- **Username**: `sa`
- **Password**: *(leave blank)*

---

## Notes

- The H2 database is in memory, so all data resets when the application restarts.
- Uploaded images and thumbnails are stored in the `uploads/` folder.
- Thumbnail images are stored in `uploads/thumbnails/`.
- Docker packaging is provided through `Dockerfile` and `compose.yaml`, so the application can be run consistently on different machines.
