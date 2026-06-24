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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName()).orElseThrow();
    }

    // Public list of all galleries.
    @GetMapping("/public")
    public String publicGalleries(Model model) {
        List<Gallery> galleries = galleryService.getAllGalleries();
        model.addAttribute("galleries", galleries);
        return "public-galleries";
    }

    // Public view of one gallery's photos.
    @GetMapping("/public/{id}/photos")
    public String publicGalleryPhotos(@PathVariable Long id, Model model) {
        Gallery gallery = galleryService.getGalleryById(id).orElseThrow();
        List<Photo> photos = photoService.getPhotosByGallery(gallery);

        model.addAttribute("gallery", gallery);
        model.addAttribute("photos", photos);

        return "public-gallery-photos";
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
    @PostMapping("/{id}/photos/upload")
    public String uploadPhoto(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file,
                              Principal principal,
                              RedirectAttributes redirectAttributes) throws IOException {
        User user = getCurrentUser(principal);
        Gallery gallery = galleryService.getGalleryByIdAndUser(id, user).orElseThrow();

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please choose an image to upload.");
            return "redirect:/galleries/" + id + "/photos";
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only image files can be uploaded.");
            return "redirect:/galleries/" + id + "/photos";
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        String thumbDir = System.getProperty("user.dir") + "/uploads/thumbnails/";
        Files.createDirectories(Paths.get(uploadDir));
        Files.createDirectories(Paths.get(thumbDir));

        String originalFilename = file.getOriginalFilename();
        String safeFileName = (originalFilename == null || originalFilename.isBlank()) ? "image.jpg" : originalFilename;
        String savedFileName = UUID.randomUUID() + "_" + safeFileName;
        String thumbFileName = "thumb_" + savedFileName;

        Path filePath = Paths.get(uploadDir, savedFileName);
        Path thumbPath = Paths.get(thumbDir, thumbFileName);

        Files.write(filePath, file.getBytes());

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

        redirectAttributes.addFlashAttribute("successMessage", "Photo uploaded successfully.");
        return "redirect:/galleries/" + id + "/photos";
    }

    // Delete a photo only after checking that it belongs to the selected gallery.
    @PostMapping("/{galleryId}/photos/{photoId}/delete")
    public String deletePhoto(@PathVariable Long galleryId,
                              @PathVariable Long photoId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) throws IOException {
        User user = getCurrentUser(principal);
        Gallery gallery = galleryService.getGalleryByIdAndUser(galleryId, user).orElseThrow();

        Photo photo = photoService.getPhotoById(photoId).orElseThrow();

        if (!photo.getGallery().getId().equals(gallery.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "That photo does not belong to this gallery.");
            return "redirect:/galleries/" + galleryId + "/photos";
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        String thumbDir = System.getProperty("user.dir") + "/uploads/thumbnails/";

        Files.deleteIfExists(Paths.get(uploadDir, photo.getFileName()));
        Files.deleteIfExists(Paths.get(thumbDir, "thumb_" + photo.getFileName()));

        photoService.deletePhoto(photoId);

        redirectAttributes.addFlashAttribute("successMessage", "Photo deleted successfully.");
        return "redirect:/galleries/" + galleryId + "/photos";
    }
}