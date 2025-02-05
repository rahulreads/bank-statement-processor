package com.example.bankstatementprocessor.service;

import com.example.bankstatementprocessor.model.TransactionRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

@Service
public class FileProcessingService {

    public List<TransactionRecord> parseFile(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || (!filename.endsWith(".csv") && !filename.endsWith(".xml"))) {
            throw new IllegalArgumentException("Unsupported file format: " + filename);
        }

        try {
            if (filename.endsWith(".csv")) {
                return parseCsv(file);
            } else if (filename.endsWith(".xml")) {
                return parseXml(file);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file: " + e.getMessage(), e);
        }

        throw new IllegalStateException("Unexpected file type");
    }

    private List<TransactionRecord> parseCsv(MultipartFile file) throws Exception {
        List<TransactionRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { 
                    isFirstLine = false; // Skip header
                    continue;
                }

                String[] values = line.split(",");
                if (values.length != 6) {
                    throw new IllegalArgumentException("Invalid CSV format");
                }

                String reference = values[0];     // Reference
                String accountNumber = values[1]; // Account Number
                String description = values[2];   // Description;
                double startBalance = Double.parseDouble(values[3]); // Start Balance
                double mutation = Double.parseDouble(values[4]); // Mutation
                double endBalance = Double.parseDouble(values[5]);  // End Balance;

                records.add(new TransactionRecord(reference, accountNumber, description, startBalance, mutation, endBalance));
            }
        }

        return records;
    }

    private List<TransactionRecord> parseXml(MultipartFile file) throws Exception {
        List<TransactionRecord> records = new ArrayList<>();
    
        try (InputStream inputStream = file.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
    
            NodeList nodeList = document.getElementsByTagName("record");
    
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
    
                String reference = element.getAttribute("reference");
                String accountNumber = getTextContent(element, "accountNumber");
                String description = getTextContent(element, "description");
                double startBalance = Double.parseDouble(getTextContent(element, "startBalance"));
                double mutation = Double.parseDouble(getTextContent(element, "mutation"));
                double endBalance = Double.parseDouble(getTextContent(element, "endBalance"));
    
                records.add(new TransactionRecord(reference, accountNumber, description, startBalance, mutation, endBalance));
            }
        }
    
        return records;
    }

    private String getTextContent(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        throw new IllegalArgumentException("Missing field: " + tagName);
    }
}




