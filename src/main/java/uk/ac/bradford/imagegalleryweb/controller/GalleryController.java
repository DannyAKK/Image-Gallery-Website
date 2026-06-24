package uk.ac.bradford.imagegalleryweb.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import uk.ac.bradford.imagegalleryweb.entity.Gallery;
import uk.ac.bradford.imagegalleryweb.entity.Photo;
import uk.ac.bradford.imagegalleryweb.entity.User;
import uk.ac.bradford.imagegalleryweb.repository.UserRepository;
import uk.ac.bradford.imagegalleryweb.service.GalleryService;
import uk.ac.bradford.imagegalleryweb.service.PhotoService;

@Controller
@RequestMapping("/galleries")
public class GalleryController {

    private final GalleryService galleryService;
    private final PhotoService photoService;
    private final UserRepository userRepository;

    public GalleryController(GalleryService galleryService, PhotoService photoService, UserRepository userRepository) {
        this.galleryService = galleryService;
        this.photoService = photoService;
        this.userRepository = userRepository;
    }

    // Helper method to get the currently logged-in user.
    // This keeps gallery and photo actions tied to the correct account.
    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName()).orElseThrow();
    }

    // Show only the galleries that belong to the logged-in user.
    @GetMapping
    public String listGalleries(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        List<Gallery> galleries = galleryService.getGalleriesByUser(user);
        model.addAttribute("galleries", galleries);
        return "galleries";
    }

    // Open the form for creating a new gallery.
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("gallery", new Gallery());
        return "gallery-form";
    }

    // Save a gallery and link it to the current user.
    @PostMapping("/save")
    public String saveGallery(@ModelAttribute("gallery") Gallery gallery, Principal principal) {
        User user = getCurrentUser(principal);
        gallery.setUser(user);
        galleryService.saveGallery(gallery);
        return "redirect:/galleries";
    }

    // Only allow a user to edit their own gallery.
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        Gallery gallery = galleryService.getGalleryByIdAndUser(id, user).orElseThrow();
        model.addAttribute("gallery", gallery);
        return "gallery-form";
    }

    // Delete only if the gallery belongs to the logged-in user.
    @GetMapping("/delete/{id}")
    public String deleteGallery(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);
        galleryService.deleteGallery(id, user);
        return "redirect:/galleries";
    }

    // Show all photos inside one gallery owned by the current user.
    @GetMapping("/{id}/photos")
    public String viewGalleryPhotos(@PathVariable Long id, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        Gallery gallery = galleryService.getGalleryByIdAndUser(id, user).orElseThrow();
        List<Photo> photos = photoService.getPhotosByGallery(gallery);

        model.addAttribute("gallery", gallery);
        model.addAttribute("photos", photos);

        return "gallery-photos";
    }

    // Upload a photo into the selected gallery.
    // A smaller thumbnail is also generated so the gallery page loads faster.
    @PostMapping("/{id}/photos/upload")
    public String uploadPhoto(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file,
                              Principal principal) throws IOException {
        User user = getCurrentUser(principal);
        Gallery gallery = galleryService.getGalleryByIdAndUser(id, user).orElseThrow();

        if (!file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            String thumbDir = System.getProperty("user.dir") + "/uploads/thumbnails/";
            Files.createDirectories(Paths.get(uploadDir));
            Files.createDirectories(Paths.get(thumbDir));

            String originalFilename = file.getOriginalFilename();
            String savedFileName = UUID.randomUUID() + "_" + originalFilename;
            String thumbFileName = "thumb_" + savedFileName;

            Path filePath = Paths.get(uploadDir, savedFileName);
            Path thumbPath = Paths.get(thumbDir, thumbFileName);

            Files.write(filePath, file.getBytes());

            // Precompute the thumbnail once during upload instead of resizing every time in the browser.
            Thumbnails.of(filePath.toFile())
                    .size(300, 300)
                    .keepAspectRatio(true)
                    .toFile(thumbPath.toFile());

            Photo photo = new Photo();
            photo.setFileName(savedFileName);
            photo.setFilePath("/uploads/" + savedFileName);
            photo.setThumbnailPath("/uploads/thumbnails/" + thumbFileName);
            photo.setGallery(gallery);

            photoService.savePhoto(photo);
        }

        return "redirect:/galleries/" + id + "/photos";
    }

    // Delete a photo only after checking that it belongs to the selected gallery.
    // This stops one user from deleting photos from another user's gallery.
    @PostMapping("/{galleryId}/photos/{photoId}/delete")
    public String deletePhoto(@PathVariable Long galleryId,
                              @PathVariable Long photoId,
                              Principal principal) throws IOException {
        User user = getCurrentUser(principal);
        Gallery gallery = galleryService.getGalleryByIdAndUser(galleryId, user).orElseThrow();

        Photo photo = photoService.getPhotoById(photoId).orElseThrow();

        if (!photo.getGallery().getId().equals(gallery.getId())) {
            throw new IllegalArgumentException("Photo does not belong to this gallery");
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        String thumbDir = System.getProperty("user.dir") + "/uploads/thumbnails/";

        Files.deleteIfExists(Paths.get(uploadDir, photo.getFileName()));
        Files.deleteIfExists(Paths.get(thumbDir, "thumb_" + photo.getFileName()));

        photoService.deletePhoto(photoId);

        return "redirect:/galleries/" + galleryId + "/photos";
    }
}