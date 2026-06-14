package uk.ac.bradford.imagegalleryweb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "gallery_id", nullable = false)
    private Gallery gallery;

    public Photo() {
    }

    public Photo(String fileName, String filePath, Gallery gallery) {
        this.fileName = fileName;
        this.filePath = filePath;
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

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }
}