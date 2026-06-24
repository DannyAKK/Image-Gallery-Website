package uk.ac.bradford.imagegalleryweb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import uk.ac.bradford.imagegalleryweb.entity.Gallery;
import uk.ac.bradford.imagegalleryweb.entity.User;
import uk.ac.bradford.imagegalleryweb.repository.GalleryRepository;

@Service
public class GalleryService {

    private final GalleryRepository galleryRepository;

    // Injects the gallery repository.
    public GalleryService(GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    // Gets all galleries for public viewing.
    public List<Gallery> getAllGalleries() {
        return galleryRepository.findAll();
    }

    // Gets one gallery by id for public viewing.
    public Optional<Gallery> getGalleryById(Long id) {
        return galleryRepository.findById(id);
    }

    // Gets all galleries for one user.
    public List<Gallery> getGalleriesByUser(User user) {
        return galleryRepository.findByUser(user);
    }

    // Gets one gallery for one user.
    public Optional<Gallery> getGalleryByIdAndUser(Long id, User user) {
        return galleryRepository.findByIdAndUser(id, user);
    }

    // Saves a gallery.
    public Gallery saveGallery(Gallery gallery) {
        return galleryRepository.save(gallery);
    }

    // Deletes the gallery if it belongs to the user.
    public void deleteGallery(Long id, User user) {
        galleryRepository.findByIdAndUser(id, user)
                .ifPresent(galleryRepository::delete);
    }
}