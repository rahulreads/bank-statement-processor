package com.example.bankstatementprocessor.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FileUploadControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //  Test for CSV file with valid transactions
    @Test
    void testUploadCsvFile_Success() throws Exception {
        // Mock a valid CSV file
        String csvData = "Reference,AccountNumber,Description,Start Balance,Mutation,End Balance\n" +
                         "1001,NL91RABO0315273637,Payment,100.00,-20.00,80.00\n";
        MockMultipartFile file = new MockMultipartFile("file", "transactions.csv",
                "text/csv", csvData.getBytes());

        String response = mockMvc.perform(multipart("/api/v1/upload").file(file))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(response);
        assertThat(jsonResponse.get("message").asText()).isEqualTo("All records are valid.");
    }

    // Test for CSV file with duplicate transaction reference
    @Test
    void testUploadCsvFile_WithErrors() throws Exception {
        // Mock a CSV file with duplicate reference
        String csvData = "Reference,AccountNumber,Description,Start Balance,Mutation,End Balance\n" +
                         "1001,NL91RABO0315273637,Payment,100.00,-20.00,80.00\n" +
                         "1001,NL91RABO0315273637,Duplicate,200.00,-50.00,150.00\n";
        MockMultipartFile file = new MockMultipartFile("file", "transactions.csv",
                "text/csv", csvData.getBytes());

        String response = mockMvc.perform(multipart("/api/v1/upload").file(file))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(response);
        assertThat(jsonResponse.isArray()).isTrue();
        assertThat(jsonResponse.get(0).get("transactionRef").asText()).isEqualTo("1001");
        assertThat(jsonResponse.get(0).get("errorMessage").asText()).isEqualTo("Duplicate reference");
    }

    //  Test for XML file with valid transactions
    @Test
    void testUploadXmlFile_Success() throws Exception {
        // Mock a valid XML file
        String xmlData = """
            <records>
                <record reference="1001">
                    <accountNumber>NL91RABO0315273637</accountNumber>
                    <description>Payment</description>
                    <startBalance>100.00</startBalance>
                    <mutation>-20.00</mutation>
                    <endBalance>80.00</endBalance>
                </record>
            </records>
            """;

        MockMultipartFile file = new MockMultipartFile("file", "transactions.xml",
                "text/xml", xmlData.getBytes());

        String response = mockMvc.perform(multipart("/api/v1/upload").file(file))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(response);
        assertThat(jsonResponse.get("message").asText()).isEqualTo("All records are valid.");
    }

    // Test for XML file with duplicate transaction reference
    @Test
    void testUploadXmlFile_WithErrors() throws Exception {
        // Mock an XML file with duplicate transaction reference
        String xmlData = """
            <records>
                <record reference="1001">
                    <accountNumber>NL91RABO0315273637</accountNumber>
                    <description>Payment</description>
                    <startBalance>100.00</startBalance>
                    <mutation>-20.00</mutation>
                    <endBalance>80.00</endBalance>
                </record>
                <record reference="1001">
                    <accountNumber>NL91RABO0315273637</accountNumber>
                    <description>Duplicate</description>
                    <startBalance>200.00</startBalance>
                    <mutation>-50.00</mutation>
                    <endBalance>150.00</endBalance>
                </record>
            </records>
            """;

        MockMultipartFile file = new MockMultipartFile("file", "transactions.xml",
                "text/xml", xmlData.getBytes());

        String response = mockMvc.perform(multipart("/api/v1/upload").file(file))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(response);
        assertThat(jsonResponse.isArray()).isTrue();
        assertThat(jsonResponse.get(0).get("transactionRef").asText()).isEqualTo("1001");
        assertThat(jsonResponse.get(0).get("errorMessage").asText()).isEqualTo("Duplicate reference");
    }


    @Test
    void testUploadInvalidFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "transactions.txt",
                "text/plain", "Invalid File Format".getBytes());

        mockMvc.perform(multipart("/api/v1/upload").file(file))
                .andExpect(status().isInternalServerError());
    }
}
