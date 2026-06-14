package uk.ac.bradford.imagegalleryweb.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import uk.ac.bradford.imagegalleryweb.entity.Gallery;
import uk.ac.bradford.imagegalleryweb.entity.User;
import uk.ac.bradford.imagegalleryweb.repository.UserRepository;
import uk.ac.bradford.imagegalleryweb.service.GalleryService;

@Controller
@RequestMapping("/galleries")
public class GalleryController {

    private final GalleryService galleryService;
    private final UserRepository userRepository;

    public GalleryController(GalleryService galleryService, UserRepository userRepository) {
        this.galleryService = galleryService;
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
}