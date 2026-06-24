package uk.ac.bradford.imagegalleryweb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stores the saved file name on disk.
    @Column(nullable = false)
    private String fileName;

    // Path used to load the full image in the browser.
    @Column(nullable = false)
    private String filePath;

    // Separate thumbnail path so the gallery can load smaller images faster.
    @Column
    private String thumbnailPath;

    // Each photo belongs to one gallery.
    @ManyToOne
    @JoinColumn(name = "gallery_id", nullable = false)
    private Gallery gallery;

    public Photo() {
    }

    public Photo(String fileName, String filePath, String thumbnailPath, Gallery gallery) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.thumbnailPath = thumbnailPath;
        this.gallery = gallery;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }
}