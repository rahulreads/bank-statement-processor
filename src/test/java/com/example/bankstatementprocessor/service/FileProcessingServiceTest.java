package com.example.bankstatementprocessor.service;

import com.example.bankstatementprocessor.model.TransactionRecord;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import org.springframework.mock.web.MockMultipartFile;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertEquals;


class FileProcessingServiceTest {

    private final FileProcessingService fileProcessingService = new FileProcessingService();

    @Test
    void testParseFile_UnsupportedFormat() {
        MultipartFile file = mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn("invalid.txt");

        assertThrows(ResponseStatusException.class, () -> fileProcessingService.parseFile(file));
    }

    @Test
    void testParseXml_Success() throws Exception {
    String xmlData = """
        <records>
            <record reference="12345">
                <accountNumber>NL91RABO0315273637</accountNumber>
                <description>Payment</description>
                <startBalance>100.00</startBalance>
                <mutation>-20.00</mutation>
                <endBalance>80.00</endBalance>
            </record>
        </records>
        """;

    MockMultipartFile file = new MockMultipartFile("file", "transactions.xml",
            "text/xml", xmlData.getBytes(StandardCharsets.UTF_8));

    List<TransactionRecord> records = fileProcessingService.parseFile(file);
    
    assertEquals(1, records.size());
    assertEquals("12345", records.get(0).getTransactionRef());
}

}

