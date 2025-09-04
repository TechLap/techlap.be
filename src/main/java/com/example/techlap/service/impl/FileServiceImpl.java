package com.example.techlap.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.techlap.domain.respond.file.ResUploadFileDTO;
import com.example.techlap.service.FileService;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Value("${techlap.upload-file.base-uri}")
    private String baseURI;

    @Override
    public ResUploadFileDTO upload(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("File is empty");
        String name = file.getOriginalFilename();
        String ext = (name != null && name.lastIndexOf('.') != -1)
                ? name.substring(name.lastIndexOf('.') + 1).toLowerCase()
                : "";
        List<String> allowed = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        if (!allowed.contains(ext))
            throw new IllegalArgumentException("Invalid file format: " + allowed);

        // ensure dir + save
        String saved = store(file, folder); // dùng method store đã tối ưu của bạn
        return new ResUploadFileDTO(saved, Instant.now());
    }

    @Override
    public void createDirectory(String folder) throws IOException {
        Path path = buildPath(folder);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.info("Created directory: {}", path);
        } else {
            logger.debug("Directory already exists: {}", path);
        }
    }

    @Override
    public String store(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Create unique filename with UUID for better uniqueness
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String finalName = UUID.randomUUID().toString() + extension;

        Path path = buildPath(folder, finalName);

        // Ensure directory exists
        Files.createDirectories(path.getParent());

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        logger.info("File stored successfully: {}", finalName);
        return finalName;
    }

    @Override
    public long getFileLength(String fileName, String folder) throws IOException {
        Path path = buildPath(folder, fileName);

        if (!Files.exists(path) || Files.isDirectory(path)) {
            return 0;
        }

        return Files.size(path);
    }

    @Override
    public InputStreamResource getResource(String fileName, String folder)
            throws FileNotFoundException, IOException {
        Path path = buildPath(folder, fileName);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        return new InputStreamResource(new FileInputStream(path.toFile()));
    }

    @Override
    public ResponseEntity<Resource> download(String fileName, String folder) throws IOException {
        if (fileName == null || folder == null)
            throw new IllegalArgumentException("File name or folder is empty");
        long len = getFileLength(fileName, folder);
        if (len == 0)
            throw new FileNotFoundException("File not found: " + fileName);
        var resource = getResource(fileName, folder);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        String ext = fileName.lastIndexOf('.') != -1 ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
                : "";
        mediaType = switch (ext) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "pdf" -> MediaType.APPLICATION_PDF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(len)
                .contentType(mediaType)
                .body(resource);
    }

    // Helper methods
    private Path buildPath(String folder) {
        String cleanBaseURI = baseURI.replace("file:", "");
        return Paths.get(cleanBaseURI, folder);
    }

    private Path buildPath(String folder, String fileName) {
        String cleanBaseURI = baseURI.replace("file:", "");
        return Paths.get(cleanBaseURI, folder, fileName);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }
}