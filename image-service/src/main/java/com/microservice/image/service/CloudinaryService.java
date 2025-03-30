package com.microservice.image.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.microservice.image.exception.CloudinaryException;
import com.microservice.image.exception.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {
    public Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cloud_name", cloudName);
        valuesMap.put("api_key", apiKey);
        valuesMap.put("api_secret", apiSecret);
        cloudinary = new Cloudinary(valuesMap);
    }

    // chuyen anh thành file
    private File convert(MultipartFile multipartFile) throws IOException {
        try{
            File file = new File(multipartFile.getOriginalFilename());
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(multipartFile.getBytes());
            fo.close();
            return file;
        }catch (IOException e){
            log.error("Unable to convert multipart file: {}",e.getMessage());
            throw new CloudinaryException(Error.CONVERSION_FAILED);
        }
    }

    public String getImageUrl(String publicId){
        return cloudinary.url().generate(publicId);
    }

    public String getPublicId(String imageUrl){
        // url mau:
        // http://res.cloudinary.com/dgts7tmnb/image/upload/v1718723087/oax0ufrlkzdyjslbxv0c.png
        String[] parts = imageUrl.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        return publicIdWithExtension.split("\\.")[0];
    }

    // tai anh len cloud
    public Map upload(MultipartFile multipartFile){
        try{
        log.info("Uploading photo to cloud: {}", multipartFile.getOriginalFilename());
            File file = convert(multipartFile);
            Map result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            if(!Files.deleteIfExists(file.toPath())){
                log.error("Unable to upload file: {}", file.getAbsolutePath());
                throw new IOException("Unable to upload temporary file: " + file.getAbsolutePath());
            }
            return result;
        }catch (IOException e){
            log.error("Unable to upload file, error: {}", e.getMessage());
            throw new CloudinaryException(Error.UPLOAD_FAILED);
        }
    }

    // Xoa anh tren cloud
    public Map delete(String id) throws IOException {
        try{
            log.info("Deleting photo to cloud: {}", id);
            return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        }catch (IOException e){
            log.error("Unable to delete file: {}",e.getMessage());
            throw new CloudinaryException(Error.DELETE_FAILED);
        }
    }
}
