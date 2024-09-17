package org.bilaltahseen.backgroundprocessing.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bilaltahseen.backgroundprocessing.entity.FileProcessingRecord;
import org.bilaltahseen.backgroundprocessing.repository.FileProcessingRecordRepository;
import org.bilaltahseen.backgroundprocessing.storage.FileSystemStorageService;
import org.bilaltahseen.backgroundprocessing.storage.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class FileProcessingService {

    private final FileProcessingRecordRepository fileProcessingRecordRepository;
    private final FileSystemStorageService storageService;

    public Long startFileProcessing(String fileName) {

        FileProcessingRecord record = new FileProcessingRecord();
        record.setFileName(fileName);
        record.setStatus(FileProcessingRecord.Status.PENDING);
        record.setStartTime(LocalDateTime.now());

        FileProcessingRecord savedEntity = fileProcessingRecordRepository.save(record);

        return savedEntity.getId();
    }

    // Async method to handle the file processing in the background
    @Async
    public CompletableFuture<Void> processFileInBackground(Long id, String fileName) {
        FileProcessingRecord record = fileProcessingRecordRepository.findById(id).orElseThrow();

        // Update status to IN_PROGRESS
        record.setStatus(FileProcessingRecord.Status.IN_PROGRESS);
        fileProcessingRecordRepository.save(record);

        try {
            // Simulate file processing logic
            System.out.println("Processing file in background: " + fileName);
            Thread.sleep(5000); // Simulate time-consuming task

            Resource file = storageService.loadAsResource(fileName);

            // Read the file content
            Path path = file.getFile().toPath();

            List<String> lines = Files.readAllLines(path);

            // Print the content of the file
            lines.forEach(System.out::println);


            // On successful completion
            record.setStatus(FileProcessingRecord.Status.COMPLETED);
            record.setEndTime(LocalDateTime.now());
            record.setResultMessage("File processed successfully");

        } catch (Exception e) {
            // In case of an error
            record.setStatus(FileProcessingRecord.Status.FAILED);
            record.setEndTime(LocalDateTime.now());
            record.setResultMessage("Error processing file: " + e.getMessage());
        }

        // Save the final status
        fileProcessingRecordRepository.save(record);

        return CompletableFuture.completedFuture(null);
    }

    public String getStatus(Long id) {
        // Get the file processing record by id
        Optional<FileProcessingRecord> record = fileProcessingRecordRepository.findById(id);
        return record.map(FileProcessingRecord::getStatus).orElse(FileProcessingRecord.Status.FAILED).name();
    }
}
