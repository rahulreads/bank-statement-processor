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

import java.util.List;

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

        try {
            List<TransactionRecord> transactions = fileProcessingService.parseFile(file);
            List<ValidationError> errors = validationService.validateRecords(transactions);

            if (errors.isEmpty()) {
                logger.info("File processed successfully: {}", file.getOriginalFilename());
                return ResponseEntity.ok().body("{\"message\": \"All records are valid.\"}");
            } else {
                logger.warn("Validation errors found in file: {}", file.getOriginalFilename());
                return ResponseEntity.badRequest().body(errors);
            }
        } catch (Exception e) {
            logger.error("Error processing file: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("{\"error\": \"Failed to process file.\"}");
        }
    }
}

