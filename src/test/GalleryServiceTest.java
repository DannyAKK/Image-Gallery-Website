package uk.ac.bradford.imagegalleryweb.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.ac.bradford.imagegalleryweb.entity.Gallery;
import uk.ac.bradford.imagegalleryweb.entity.User;
import uk.ac.bradford.imagegalleryweb.repository.GalleryRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GalleryServiceTest {

    @Mock
    private GalleryRepository galleryRepository;

    @InjectMocks
    private GalleryService galleryService;

    @Test
    void getGalleriesByUser_returnsUserGalleries() {
        User user = new User();
        Gallery gallery = new Gallery();
        when(galleryRepository.findByUser(user)).thenReturn(List.of(gallery));

        List<Gallery> result = galleryService.getGalleriesByUser(user);

        // Checks that the service returns the galleries linked to the user.
        assertEquals(1, result.size());
        verify(galleryRepository).findByUser(user);
    }

    @Test
    void saveGallery_savesAndReturnsGallery() {
        Gallery gallery = new Gallery();
        when(galleryRepository.save(gallery)).thenReturn(gallery);

        Gallery result = galleryService.saveGallery(gallery);

        // Confirms the gallery is saved and a result is returned.
        assertNotNull(result);
        verify(galleryRepository).save(gallery);
    }

    @Test
    void deleteGallery_deletesWhenOwnerMatches() {
        User user = new User();
        Gallery gallery = new Gallery();
        when(galleryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(gallery));

        galleryService.deleteGallery(1L, user);

        // Only deletes when the gallery belongs to the current user.
        verify(galleryRepository).delete(gallery);
    }

    @Test
    void deleteGallery_doesNothingWhenNotOwner() {
        User user = new User();
        when(galleryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        galleryService.deleteGallery(1L, user);

        // Prevents deletion if the gallery is not owned by that user.
        verify(galleryRepository, never()).delete(any());
    }

    @Test
    void getGalleryByIdAndUser_returnsGalleryWhenOwner() {
        User user = new User();
        Gallery gallery = new Gallery();
        when(galleryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(gallery));

        Optional<Gallery> result = galleryService.getGalleryByIdAndUser(1L, user);

        // Verifies that the gallery can be found for its owner.
        assertTrue(result.isPresent());
    }
}