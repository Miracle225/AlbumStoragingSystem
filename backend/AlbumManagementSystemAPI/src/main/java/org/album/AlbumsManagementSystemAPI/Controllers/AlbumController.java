package org.album.AlbumsManagementSystemAPI.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.album.AlbumsManagementSystemAPI.Models.Account;
import org.album.AlbumsManagementSystemAPI.Models.Album;
import org.album.AlbumsManagementSystemAPI.Models.Photo;
import org.album.AlbumsManagementSystemAPI.Services.AccountService;
import org.album.AlbumsManagementSystemAPI.Services.AlbumService;
import org.album.AlbumsManagementSystemAPI.Services.PhotoService;
import org.album.AlbumsManagementSystemAPI.payload.album.*;
import org.album.AlbumsManagementSystemAPI.util.AppUtils.AppUtil;
import org.album.AlbumsManagementSystemAPI.util.constants.AlbumError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/v1/album")
@Tag(name = "Album controller", description = "Controller for album and photo management")
@Slf4j
public class AlbumController {
    static final String PHOTOS_FOLDER_NAME = "photos";
    static final String THUMBNAIL_FOLDER_NAME = "thumbnails";
    static final int THUMBNAIL_WIDTH = 300;
    private final AccountService accountService;
    private final AlbumService albumService;
    private final PhotoService photoService;

    @Autowired
    public AlbumController(AccountService accountService,
                           AlbumService albumService,
                           PhotoService photoService) {
        this.accountService = accountService;
        this.albumService = albumService;
        this.photoService = photoService;
    }

    @PostMapping(value = "/add", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new album")
    @ApiResponse(responseCode = "400", description = "Please enter a valid data for album")
    @ApiResponse(responseCode = "200", description = "Album added")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<AlbumViewDTO> addAlbum(@Valid @RequestBody AlbumDTO albumDTO, Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> opAccount = accountService.findByEmail(email);
            Account account = opAccount.get();
            Album album = new Album(albumDTO.getName(), albumDTO.getDescription(), account);
            album = albumService.save(album);
            return ResponseEntity.ok(new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), null));
        } catch (Exception e) {
            log.debug("{}: {}", AlbumError.ADD_ALBUM_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/getAll", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of albums")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "List album api")
    @SecurityRequirement(name = "album-demo-api")
    public List<AlbumViewDTO> getAllAlbums(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> opAccount = accountService.findByEmail(email);
        Account account = opAccount.get();
        List<AlbumViewDTO> albums = new ArrayList<>();
        for (Album album : albumService.findByAccountId(account.getId())) {
            List<PhotoDTO> photos = new ArrayList<>();
            for (Photo photo : photoService.findAllByAlbumId(album.getId())) {
                String link = "/" + album.getId() + "/photos/" + photo.getId() + "/download-photo";
                photos.add(new PhotoDTO(photo.getId(), photo.getName(),
                        photo.getDescription(), photo.getFileName(), link));
            }
            albums.add(new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), photos));
        }
        return albums;
    }

    @PostMapping(value = "/{album_id}/upload-photos", consumes = {"multipart/form-data"})
    @SecurityRequirement(name = "album-demo-api")
    @Operation(summary = "Upload photo into album")
    @ApiResponse(responseCode = "400", description = "Please check the payload of token")
    public ResponseEntity<List<HashMap<String, List<?>>>> photos(@RequestPart(required = true) MultipartFile[] files,
                                                                      @PathVariable Long album_id,
                                                                      Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> opAccount = accountService.findByEmail(email);
        Account account = opAccount.get();
        Optional<Album> opAlbum = albumService.findById(album_id);
        Album album;
        if (opAlbum.isPresent()) {
            album = opAlbum.get();
            if (!account.getId().equals(album.getAccount().getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<PhotoViewDTO> fileNamesWithSuccess = new ArrayList<>();
        List<String> fileNamesWithError = new ArrayList<>();
        Arrays.stream(files).forEach(file -> {
            String contentType = file.getContentType();
            if (contentType.equals("image/png")
                    || contentType.equals("image/jpeg")
                    || contentType.equals("image/jpg")) {
                int length = 10;
                boolean useLetters = true;
                boolean useNumbers = true;
                try {
                    String fileName = file.getOriginalFilename();
                    String generatedString = getAlphaNumericString(length);
                    String finalPhotoName = generatedString + fileName;
                    String absoluteFileLocation = AppUtil.getPhotoUploadPath(finalPhotoName, PHOTOS_FOLDER_NAME, album_id);
                    Path path = Paths.get(absoluteFileLocation);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    Photo photo = new Photo();
                    photo.setName(fileName);
                    photo.setFileName(finalPhotoName);
                    photo.setOriginalFileName(fileName);
                    photo.setAlbum(album);
                    photoService.save(photo);
                    PhotoViewDTO photoViewDTO = new PhotoViewDTO(photo.getId(),photo.getName(),photo.getDescription());
                    fileNamesWithSuccess.add(photoViewDTO);
                    BufferedImage thumbImg = AppUtil.getThumbnail(file, THUMBNAIL_WIDTH);
                    File thumbnailLocation = new File(AppUtil.getPhotoUploadPath(finalPhotoName, THUMBNAIL_FOLDER_NAME, album_id));
                    ImageIO.write(thumbImg, file.getContentType().split("/")[1], thumbnailLocation);
                } catch (Exception e) {
                    log.debug("{}{}", AlbumError.PHOTO_UPLOAD_ERROR, e.getMessage());
                    fileNamesWithError.add(file.getOriginalFilename());
                }
            } else {
                fileNamesWithError.add(file.getOriginalFilename());
            }
        });
        HashMap<String, List<?>> result = new HashMap<>();
        result.put("SUCCESS", fileNamesWithSuccess);
        result.put("ERROR", fileNamesWithError);
        List<HashMap<String, List<?>>> response = new ArrayList<>();
        response.add(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{album_id}/photos/{photo_id}/download-photo")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<?> downloadPhoto(@PathVariable("album_id") Long album_id,
                                           @PathVariable("photo_id") Long photo_id,
                                           Authentication authentication) {
        return downloadFile(album_id, photo_id, PHOTOS_FOLDER_NAME, authentication);
    }

    @GetMapping("/{album_id}/photos/{photo_id}/download-thumbnail")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<?> downloadThumbnail(@PathVariable("album_id") Long album_id,
                                               @PathVariable("photo_id") Long photo_id,
                                               Authentication authentication) {
        return downloadFile(album_id, photo_id, THUMBNAIL_FOLDER_NAME, authentication);
    }

    @GetMapping(value = "/{album_id}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Album by ID")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "Album by ID")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<AlbumViewDTO> getAlbumById(@PathVariable Long album_id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> opAccount = accountService.findByEmail(email);
        Account account = opAccount.get();
        Optional<Album> opAlbum = albumService.findById(album_id);
        Album album;
        if (opAlbum.isPresent()) {
            album = opAlbum.get();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (!account.getId().equals(album.getAccount().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        List<PhotoDTO> photos = new ArrayList<>();
        for (Photo photo : photoService.findAllByAlbumId(album.getId())) {
            String link = "/" + album.getId() + "/photos/" + photo.getId() + "/download-photo";
            photos.add(new PhotoDTO(photo.getId(), photo.getName(),
                    photo.getDescription(), photo.getFileName(), link));
        }
        return ResponseEntity.ok(new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), photos));
    }

    @PutMapping(value = "/{album_id}/update", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Update an album")
    @ApiResponse(responseCode = "400", description = "Please enter a valid data for album")
    @ApiResponse(responseCode = "204", description = "Album updated")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<AlbumViewDTO> updateAlbum(@Valid @RequestBody AlbumDTO albumDTO,
                                                    Authentication authentication,
                                                    @PathVariable Long album_id) {
        try {
            String email = authentication.getName();
            Optional<Account> opAccount = accountService.findByEmail(email);
            Account account = opAccount.get();
            Optional<Album> opAlbum = albumService.findById(album_id);
            Album album;
            if (opAlbum.isPresent()) {
                album = opAlbum.get();
                if (!account.getId().equals(album.getAccount().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            album.setName(albumDTO.getName());
            album.setDescription(albumDTO.getDescription());
            album = albumService.save(album);
            List<PhotoDTO> photos = new ArrayList<>();
            for (Photo photo : photoService.findAllByAlbumId(album.getId())) {
                String link = "/" + album.getId() + "/photos/" + photo.getId() + "/download-photo";
                photos.add(new PhotoDTO(photo.getId(), photo.getName(),
                        photo.getDescription(), photo.getFileName(), link));
            }
            return ResponseEntity.ok(new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), photos));
        }catch (Exception e){
            log.debug("{}{}", AlbumError.UPDATE_ALBUM_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @PutMapping(value = "/{album_id}/photos/{photo_id}/update", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Update a photo")
    @ApiResponse(responseCode = "400", description = "Please enter a valid data for photo")
    @ApiResponse(responseCode = "204", description = "Photo updated")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<PhotoViewDTO> updatePhoto(@Valid @RequestBody PhotoPayloadDTO photoDTO,
                                                    Authentication authentication,
                                                    @PathVariable Long album_id,
                                                    @PathVariable Long photo_id) {
        try {
            String email = authentication.getName();
            Optional<Account> opAccount = accountService.findByEmail(email);
            Account account = opAccount.get();
            Optional<Album> opAlbum = albumService.findById(album_id);
            Album album;
            if (opAlbum.isPresent()) {
                album = opAlbum.get();
                if (!account.getId().equals(album.getAccount().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
           Optional<Photo> opPhoto = photoService.findById(photo_id);
            if(opPhoto.isPresent()){
                Photo photo = opPhoto.get();
                if(!photo.getAlbum().getId().equals(album_id)){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                photo.setName(photoDTO.getName());
                photo.setDescription(photoDTO.getDescription());
                photoService.save(photo);
                PhotoViewDTO photoViewDTO = new PhotoViewDTO(photo.getId(),photoDTO.getName(),photoDTO.getDescription());
                return ResponseEntity.ok(photoViewDTO);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }catch (Exception e){
            log.debug("{}{}", AlbumError.UPDATE_PHOTO_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @DeleteMapping(value = "/{album_id}/photos/{photo_id}/delete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Delete a photo")
    @ApiResponse(responseCode = "202", description = "Photo deleted")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<String> deletePhoto(Authentication authentication,
                                                    @PathVariable Long album_id,
                                                    @PathVariable Long photo_id) {
        try {
            String email = authentication.getName();
            Optional<Account> opAccount = accountService.findByEmail(email);
            Account account = opAccount.get();
            Optional<Album> opAlbum = albumService.findById(album_id);
            Album album;
            if (opAlbum.isPresent()) {
                album = opAlbum.get();
                if (!account.getId().equals(album.getAccount().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            Optional<Photo> opPhoto = photoService.findById(photo_id);
            if(opPhoto.isPresent()){
                Photo photo = opPhoto.get();
                if(!photo.getAlbum().getId().equals(album_id)){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                AppUtil.deletePhotoFromPath(photo.getFileName(),PHOTOS_FOLDER_NAME,album_id);
                AppUtil.deletePhotoFromPath(photo.getFileName(),THUMBNAIL_FOLDER_NAME,album_id);
                photoService.deletePhoto(photo);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }catch (Exception e){
            log.debug("{}{}", AlbumError.DELETE_PHOTO_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @DeleteMapping(value = "/{album_id}/delete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Delete an album")
    @ApiResponse(responseCode = "202", description = "Album deleted")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<String> deleteAlbum(Authentication authentication,
                                              @PathVariable Long album_id) {
        try {
            String email = authentication.getName();
            Optional<Account> opAccount = accountService.findByEmail(email);
            Account account = opAccount.get();
            Optional<Album> opAlbum = albumService.findById(album_id);
            Album album;
            if (opAlbum.isPresent()) {
                album = opAlbum.get();
                if (!account.getId().equals(album.getAccount().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            for(Photo photo:photoService.findAllByAlbumId(album_id)){
                AppUtil.deletePhotoFromPath(photo.getFileName(),PHOTOS_FOLDER_NAME,album_id);
                AppUtil.deletePhotoFromPath(photo.getFileName(),THUMBNAIL_FOLDER_NAME,album_id);
                photoService.deletePhoto(photo);
            }
            albumService.deleteAlbum(album);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
        }catch (Exception e){
            log.debug("{}{}", AlbumError.DELETE_ALBUM_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    public ResponseEntity<?> downloadFile(Long album_id, Long photo_id,
                                          String folderName, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> opAccount = accountService.findByEmail(email);
        Account account = opAccount.get();
        Optional<Album> opAlbum = albumService.findById(album_id);
        Album album;
        if (opAlbum.isPresent()) {
            album = opAlbum.get();
            if (!account.getId().equals(album.getAccount().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<Photo> opPhoto = photoService.findById(photo_id);
        if (opPhoto.isPresent()) {
            Photo photo = opPhoto.get();
            if(!photo.getAlbum().getId().equals(album_id)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            Resource resource;
            try {
                resource = AppUtil.getFileAsResource(album_id, folderName, photo.getFileName());
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }
            if (resource == null) {
                return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
            }
            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + photo.getOriginalFileName() + "\"";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    static String getAlphaNumericString(int n) {

        // choose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
}
