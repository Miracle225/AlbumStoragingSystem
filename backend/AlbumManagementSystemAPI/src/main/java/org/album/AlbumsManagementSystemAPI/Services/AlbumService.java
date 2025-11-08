package org.album.AlbumsManagementSystemAPI.Services;

import org.album.AlbumsManagementSystemAPI.Models.Album;
import org.album.AlbumsManagementSystemAPI.Repositories.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Album save(Album album) {
        return albumRepository.save(album);
    }

    public Optional<Album> findById(Long id) {
        return albumRepository.findById(id);
    }

    public List<Album> findByAccountId(Long id) {
        return albumRepository.findByAccount_id(id);
    }
    public void deleteAlbum(Album album){
        albumRepository.delete(album);
    }
}
