package org.album.AlbumsManagementSystemAPI.util.AppUtils;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class AppUtil {
    public static final String PATH = "src\\main\\resources\\static\\uploads\\";
    public static String getPhotoUploadPath(String fileName, String folderName, Long albumId) throws IOException {
        String path = PATH + albumId + "\\" + folderName;
        Files.createDirectories(Paths.get(path));
        return new File(path).getAbsolutePath() + "\\" + fileName;
    }

    public static BufferedImage getThumbnail(MultipartFile originalFile, Integer width) throws IOException {
        BufferedImage thumbImg;
        BufferedImage img = ImageIO.read(originalFile.getInputStream());
        thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, Scalr.OP_ANTIALIAS);
        return thumbImg;
    }
    public static Resource getFileAsResource(Long albumId,String folderName,String fileName) throws IOException{
        String location = PATH + albumId + "\\" + folderName + "\\" + fileName;
        File file = new File(location);
        if(file.exists()){
            Path path = Paths.get(file.getAbsolutePath());
            return new UrlResource(path.toUri());
        }else {
            return null;
        }
    }

    public static boolean deletePhotoFromPath(String fileName, String photosFolderName, Long albumId) {
        try{
            File file = new File(PATH + albumId + "\\" + photosFolderName + "\\" + fileName);// file to be deleted
            return file.delete();
        }catch (Exception e){
            log.debug(e.getMessage());
            return false;
        }
    }
}
