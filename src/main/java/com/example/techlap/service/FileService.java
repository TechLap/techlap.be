package com.example.techlap.service;

import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.techlap.domain.respond.file.ResUploadFileDTO;

public interface FileService {

    void createDirectory(String folder) throws Exception;

    ResUploadFileDTO upload(MultipartFile file, String folder) throws IOException;

    String store(MultipartFile file, String folder) throws Exception;

    long getFileLength(String fileName, String folder) throws Exception;

    InputStreamResource getResource(String fileName, String folder) throws Exception;

    ResponseEntity<Resource> download(String fileName, String folder) throws IOException;
}
