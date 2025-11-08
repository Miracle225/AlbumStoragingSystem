package org.album.AlbumsManagementSystemAPI.Services;

import org.album.AlbumsManagementSystemAPI.Models.Photo;
import org.album.AlbumsManagementSystemAPI.Repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;

    @Autowired
    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }
    public Photo save(Photo photo){
        return photoRepository.save(photo);
    }
    public Optional<Photo> findById(Long id){
        return photoRepository.findById(id);
    }
    public List<Photo> findAllByAlbumId(Long id){
        return photoRepository.findAllByAlbum_id(id);
    }
    public void deletePhoto(Photo photo){
        photoRepository.delete(photo);
    }
}
