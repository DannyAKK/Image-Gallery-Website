package uk.ac.bradford.imagegalleryweb.repository;

import uk.ac.bradford.imagegalleryweb.entity.Gallery;
import uk.ac.bradford.imagegalleryweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    List<Gallery> findByUser(User user);
    Optional<Gallery> findByIdAndUser(Long id, User user);
}