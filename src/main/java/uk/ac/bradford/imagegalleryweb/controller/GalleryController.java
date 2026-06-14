package uk.ac.bradford.imagegalleryweb.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

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

    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName()).orElseThrow();
    }

    @GetMapping
    public String listGalleries(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        List<Gallery> galleries = galleryService.getGalleriesByUser(user);
        model.addAttribute("galleries", galleries);
        return "galleries";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("gallery", new Gallery());
        return "gallery-form";
    }

    @PostMapping("/save")
    public String saveGallery(@ModelAttribute("gallery") Gallery gallery, Principal principal) {
        User user = getCurrentUser(principal);
        gallery.setUser(user);
        galleryService.saveGallery(gallery);
        return "redirect:/galleries";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        Gallery gallery = galleryService.getGalleryByIdAndUser(id, user).orElseThrow();
        model.addAttribute("gallery", gallery);
        return "gallery-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteGallery(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);
        galleryService.deleteGallery(id, user);
        return "redirect:/galleries";
    }

    @GetMapping("/{id}/photos")
    public String viewGalleryPhotos(@PathVariable Long id, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        Gallery gallery = galleryService.getGalleryByIdAndUser(id, user).orElseThrow();
        List<Photo> photos = photoService.getPhotosByGallery(gallery);

        model.addAttribute("gallery", gallery);
        model.addAttribute("photos", photos);

        return "gallery-photos";
    }

    @PostMapping("/{id}/photos/upload")
    public String uploadPhoto(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file,
                              Principal principal) throws IOException {
        User user = getCurrentUser(principal);
        Gallery gallery = galleryService.getGalleryByIdAndUser(id, user).orElseThrow();

        if (!file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            String originalFilename = file.getOriginalFilename();
            String savedFileName = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = Paths.get(uploadDir, savedFileName);
            Files.write(filePath, file.getBytes());

            Photo photo = new Photo();
            photo.setFileName(savedFileName);
            photo.setFilePath("/uploads/" + savedFileName);
            photo.setGallery(gallery);

            photoService.savePhoto(photo);
        }

        return "redirect:/galleries/" + id + "/photos";
    }

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
        Path filePath = Paths.get(uploadDir, photo.getFileName());
        Files.deleteIfExists(filePath);

        photoService.deletePhoto(photoId);

        return "redirect:/galleries/" + galleryId + "/photos";
    }
}
