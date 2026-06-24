package uk.ac.bradford.imagegalleryweb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import uk.ac.bradford.imagegalleryweb.entity.Gallery;
import uk.ac.bradford.imagegalleryweb.entity.Photo;
import uk.ac.bradford.imagegalleryweb.repository.PhotoRepository;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    // Injects the photo repository.
    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    // Gets all photos in one gallery.
    public List<Photo> getPhotosByGallery(Gallery gallery) {
        return photoRepository.findByGallery(gallery);
    }

    // Saves one photo.
    public void savePhoto(Photo photo) {
        photoRepository.save(photo);
    }

    // Gets one photo by id.
    public Optional<Photo> getPhotoById(Long id) {
        return photoRepository.findById(id);
    }

    // Deletes one photo by id.
    public void deletePhoto(Long id) {
        photoRepository.deleteById(id);
    }
}