package org.album.AlbumsManagementSystemAPI.Repositories;

import org.album.AlbumsManagementSystemAPI.Models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {
public List<Photo> findAllByAlbum_id(Long id);
}
