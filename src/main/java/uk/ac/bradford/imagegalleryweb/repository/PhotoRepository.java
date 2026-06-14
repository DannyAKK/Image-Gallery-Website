package uk.ac.bradford.imagegalleryweb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.ac.bradford.imagegalleryweb.entity.Gallery;
import uk.ac.bradford.imagegalleryweb.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByGallery(Gallery gallery);
}