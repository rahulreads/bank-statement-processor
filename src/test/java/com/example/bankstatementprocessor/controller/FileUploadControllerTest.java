package com.example.bankstatementprocessor.controller;

import com.example.bankstatementprocessor.service.FileProcessingService;
import com.example.bankstatementprocessor.service.ValidationService;
import com.example.bankstatementprocessor.model.ValidationError;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;  
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FileUploadControllerTest {

    @Mock
    private FileProcessingService fileProcessingService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private FileUploadController fileUploadController;

    @Test
    void testUploadFile_Success() {
        MockMultipartFile file = new MockMultipartFile("file", "transactions.csv",
                "text/csv", "Sample Data".getBytes(StandardCharsets.UTF_8));

        when(fileProcessingService.parseFile(file)).thenReturn(Collections.emptyList());
        when(validationService.validateRecords(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = fileUploadController.uploadFile(file);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUploadFile_ValidationErrors() {
        MockMultipartFile file = new MockMultipartFile("file", "transactions.csv",
                "text/csv", "Sample Data".getBytes(StandardCharsets.UTF_8));

        when(fileProcessingService.parseFile(file)).thenReturn(Collections.emptyList());
        when(validationService.validateRecords(Collections.emptyList())).thenReturn(
                List.of(new com.example.bankstatementprocessor.model.ValidationError("123", "Duplicate reference"))
        );

        ResponseEntity<?> response = fileUploadController.uploadFile(file);
        assertEquals(400, response.getStatusCodeValue());
    }
}
