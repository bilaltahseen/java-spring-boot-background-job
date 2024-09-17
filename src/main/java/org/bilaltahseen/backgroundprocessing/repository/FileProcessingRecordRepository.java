package org.bilaltahseen.backgroundprocessing.repository;

import org.bilaltahseen.backgroundprocessing.entity.FileProcessingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FileProcessingRecordRepository extends JpaRepository<FileProcessingRecord, Long> {
    Optional<FileProcessingRecord> findByFileName(String fileName);
}
