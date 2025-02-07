package com.example.bankstatementprocessor.controller;

import com.example.bankstatementprocessor.service.FileProcessingService;
import com.example.bankstatementprocessor.service.ValidationService;
import com.example.bankstatementprocessor.model.TransactionRecord;
import com.example.bankstatementprocessor.model.ValidationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final FileProcessingService fileProcessingService;
    private final ValidationService validationService;

    public FileUploadController(FileProcessingService fileProcessingService, ValidationService validationService) {
        this.fileProcessingService = fileProcessingService;
        this.validationService = validationService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Received file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Uploaded file is empty"));
        }

        try {
            List<TransactionRecord> records = fileProcessingService.parseFile(file);
            List<ValidationError> errors = validationService.validateRecords(records);

            if (errors.isEmpty()) {
                logger.info("File processed successfully: {}", file.getOriginalFilename());
                return ResponseEntity.ok(Collections.singletonMap("message", "All records are valid."));
            } else {
                logger.warn("Validation errors found in file: {}", file.getOriginalFilename());
                return ResponseEntity.badRequest().body(errors);
            }
        } catch (IllegalArgumentException e) {
            logger.error("File format error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error processing file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to process file."));
        }
    }
}