package org.bilaltahseen.backgroundprocessing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_processing")
@Data
public class FileProcessingRecord  {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "result_message")
    private String resultMessage;

    // Enum for tracking status
    public enum Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

}
