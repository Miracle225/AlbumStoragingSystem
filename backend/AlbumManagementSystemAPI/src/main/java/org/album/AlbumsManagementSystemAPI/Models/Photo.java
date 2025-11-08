package org.album.AlbumsManagementSystemAPI.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photo")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String description;

    private String originalFileName;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "album_id", referencedColumnName = "id", nullable = false)
    private Album album;

    public Photo(String name, String description, String originalFileName, String fileName, Album album) {
        this.name = name;
        this.description = description;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.album = album;
    }
}
