# Image Gallery Web Application

A Spring Boot web application that allows registered users to create photo galleries and upload images. Built as part of a university placement project at the University of Bradford for the company, 'The Curve'.

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
- Unit testing with JUnit 5 and Maven Wrappe

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

---

## Requirements

- Java 21 or higher

No other installation is needed as H2 database is embedded and starts automatically.

---

## How to Run

1. Clone the repository:
   ```
   git clone https://github.com/DannyAKK/Image-Gallery-Website.git
   ```

2. Navigate into the project folder:
   ```
   cd Image-Gallery-Website
   ```

3. Build and run the application:
   ```
   .\mvnw.cmd spring-boot:run 
   ```

4. Open your browser and go to:
   ```
   Login page: http://localhost:8080 
   Public Galleries Page: http://localhost:8080/galleries/public 
   ```

5. Register a new account and start creating galleries.

---

## Running Tests
On Windows:

```bash
.\mvnw.cmd test
```

On macOS/Linux:

```bash
./mvnw test
```

Tests are run using JUnit 5 through the Maven Wrapper.
---

## Project Structure

```
src/
  main/
    java/uk/ac/bradford/imagegalleryweb/
      config/         - Web MVC configuration (static resource handlers)
      controller/     - HTTP request handlers (GalleryController, CustomErrorController)
      entity/         - JPA database models (User, Gallery, Photo)
      repository/     - Spring Data JPA repository interfaces
      security/       - Spring Security configuration
      service/        - Business logic (GalleryService, PhotoService)
    resources/
      templates/      - Thymeleaf HTML templates
        error/        - Custom error pages (404, 403, 500)
      static/
        css/          - Stylesheet (style.css)
  test/
    java/             - Unit tests (GalleryServiceTest)

uploads/              - Uploaded images stored here at runtime (excluded from git)
```

---

## H2 Database Console

The H2 in memory database console is available at:

```
http://localhost:8080/h2-console
```

Use the following connection settings:

- **JDBC URL**: `jdbc:h2:mem:imagegallery`
- **Username**: `sa`
- **Password**: *(leave blank)*

---

## Notes

- The H2 database is in memory, all data resets when the application restarts
- Uploaded images and thumbnails are stored in the `uploads/` folder in the project root
- The `uploads/` folder is excluded from version control via `.gitignore`
- Thumbnail images are stored in `uploads/thumbnails/`
