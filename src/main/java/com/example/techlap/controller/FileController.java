package com.example.techlap.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.respond.file.ResUploadFileDTO;
import com.example.techlap.service.FileService;

import lombok.AllArgsConstructor;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class FileController {

    private final FileService fileService;

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) throws IOException {
        return ResponseEntity.ok(fileService.upload(file, folder));
    }

    @GetMapping("/files")
    @ApiMessage("Download single file")
    public ResponseEntity<Resource> download(
            @RequestParam("fileName") String fileName,
            @RequestParam("folder") String folder) throws IOException {
        return fileService.download(fileName, folder);
    }
}
