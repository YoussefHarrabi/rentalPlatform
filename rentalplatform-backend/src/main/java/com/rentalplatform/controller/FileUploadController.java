package com.rentalplatform.controller;

import com.rentalplatform.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = fileStorageService.storeFile(file);
            String fileUrl = "http://localhost:8081" + fileStorageService.getFileUrl(filename);

            Map<String, String> response = new HashMap<>();
            response.put("filename", filename);
            response.put("fileUrl", fileUrl);
            response.put("message", "Fichier uploadé avec succès");

            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{filename}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String filename) {
        try {
            fileStorageService.deleteFile(filename);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Fichier supprimé avec succès");

            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}