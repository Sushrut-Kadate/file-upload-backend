package com.example.filedemo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.model.DBFile;
import com.example.filedemo.model.User;
import com.example.filedemo.repository.DBFileRepository;
import com.example.filedemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class DBFileStorageService {

    @Autowired
    private DBFileRepository dbFileRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserRepository userRepository;

    // Get current logged in user
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public DBFile storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );

            String fileUrl = (String) uploadResult.get("secure_url");

            // Save with current user
            User currentUser = getCurrentUser();
            DBFile dbFile = new DBFile(fileName, file.getContentType(), fileUrl, currentUser);

            return dbFileRepository.save(dbFile);

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    // Get only current user's files
    public List<DBFile> getAllFiles() {
        User currentUser = getCurrentUser();
        return dbFileRepository.findByUploadedBy(currentUser);
    }

    public DBFile getFile(String fileId) {
        User currentUser = getCurrentUser();
        DBFile dbFile = dbFileRepository.findById(fileId)
                .orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));

        // Check if file belongs to current user
        if (!dbFile.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied!");
        }

        return dbFile;
    }

    public void deleteFile(String fileId) {
        DBFile dbFile = getFile(fileId);

        try {
            String fileUrl = dbFile.getFileUrl();
            String publicId = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.lastIndexOf("."));
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            dbFileRepository.deleteById(fileId);

        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileId + ". Please try again!", ex);
        }
    }
}