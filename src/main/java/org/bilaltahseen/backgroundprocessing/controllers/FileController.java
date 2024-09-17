package org.bilaltahseen.backgroundprocessing.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bilaltahseen.backgroundprocessing.dto.FileRequest;
import org.bilaltahseen.backgroundprocessing.services.FileProcessingService;
import org.bilaltahseen.backgroundprocessing.storage.FileSystemStorageService;
import org.bilaltahseen.backgroundprocessing.storage.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileController {

    FileProcessingService fileProcessingService;
    FileSystemStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        storageService.store(file);
        return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully: " + file.getOriginalFilename());
    }

    @PostMapping("/process")
    public ResponseEntity<?> processFile(@RequestBody FileRequest fileRequest) {

        if (!storageService.isFileExist(fileRequest.getFileName())) {
            return ResponseEntity.notFound().build();
        }

        Long id = fileProcessingService.startFileProcessing(fileRequest.getFileName());
        fileProcessingService.processFileInBackground(id, fileRequest.getFileName());
        return ResponseEntity.ok("File processing started. Check status using /status/" + id);
    }

    @GetMapping("/status/{id}")
    public String getStatus(@PathVariable("id") Long id) {
        return "Processing status: " + fileProcessingService.getStatus(id);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
