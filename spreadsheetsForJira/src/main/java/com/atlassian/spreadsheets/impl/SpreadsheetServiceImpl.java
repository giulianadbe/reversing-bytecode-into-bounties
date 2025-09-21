package com.atlassian.spreadsheets.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.spreadsheets.api.SpreadsheetService;
import javax.inject.Named;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ExportAsService ({SpreadsheetService.class})
@Named ("spreadsheetService")
public class SpreadsheetServiceImpl implements SpreadsheetService {

    @Override
    public String processSpreadsheetXml(String xmlContent) throws Exception {
        // INTENTIONAL VULNERABILITY: XXE vulnerability due to not disabling external entities
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        // These security features are intentionally NOT set to create XXE vulnerability:
        // factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        // factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        // factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        // factory.setExpandEntityReferences(false);
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
        
        // Check if it's a testsuite format or regular spreadsheet format
        if (document.getDocumentElement().getNodeName().equals("testsuite")) {
            return extractTestsuiteData(document);
        } else {
            return extractSpreadsheetData(document);
        }
    }

    @Override
    public String processSpreadsheetFile(InputStream inputStream) throws Exception {
        // INTENTIONAL VULNERABILITY: Same XXE vulnerability in file processing
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        
        // Check if it's a testsuite format or regular spreadsheet format
        if (document.getDocumentElement().getNodeName().equals("testsuite")) {
            return extractTestsuiteData(document);
        } else {
            return extractSpreadsheetData(document);
        }
    }

    @Override
    public boolean validateSpreadsheet(String xmlContent) {
        try {
            // INTENTIONAL VULNERABILITY: Validation also uses vulnerable XML parsing
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String extractTestsuiteData(Document document) {
        StringBuilder result = new StringBuilder();
        result.append("{\n  \"testsuite\": {\n");
        
        Element testsuiteElement = document.getDocumentElement();
        result.append("    \"tests\": \"").append(testsuiteElement.getAttribute("tests")).append("\",\n");
        result.append("    \"failures\": \"").append(testsuiteElement.getAttribute("failures")).append("\",\n");
        result.append("    \"name\": \"").append(testsuiteElement.getAttribute("name")).append("\",\n");
        result.append("    \"time\": \"").append(testsuiteElement.getAttribute("time")).append("\",\n");
        
        // Extract properties (this is where XXE content will appear)
        NodeList properties = document.getElementsByTagName("property");
        result.append("    \"properties\": [\n");
        for (int i = 0; i < properties.getLength(); i++) {
            Node propNode = properties.item(i);
            if (propNode.getNodeType() == Node.ELEMENT_NODE) {
                Element propElement = (Element) propNode;
                result.append("      {\n");
                result.append("        \"name\": \"").append(propElement.getAttribute("name")).append("\",\n");
                result.append("        \"value\": \"").append(escapeJson(propElement.getTextContent())).append("\"\n");
                result.append("      }");
                if (i < properties.getLength() - 1) result.append(",");
                result.append("\n");
            }
        }
        result.append("    ],\n");
        
        // Extract test cases (this is also where XXE content can appear)
        NodeList testcases = document.getElementsByTagName("testcase");
        result.append("    \"testcases\": [\n");
        for (int i = 0; i < testcases.getLength(); i++) {
            Node testNode = testcases.item(i);
            if (testNode.getNodeType() == Node.ELEMENT_NODE) {
                Element testElement = (Element) testNode;
                result.append("      {\n");
                result.append("        \"classname\": \"").append(testElement.getAttribute("classname")).append("\",\n");
                result.append("        \"name\": \"").append(testElement.getAttribute("name")).append("\",\n");
                result.append("        \"time\": \"").append(testElement.getAttribute("time")).append("\",\n");
                
                // Check for failure elements (where XXE content often appears)
                NodeList failures = testElement.getElementsByTagName("failure");
                if (failures.getLength() > 0) {
                    result.append("        \"failure\": \"").append(escapeJson(failures.item(0).getTextContent())).append("\"\n");
                } else {
                    result.append("        \"failure\": null\n");
                }
                
                result.append("      }");
                if (i < testcases.getLength() - 1) result.append(",");
                result.append("\n");
            }
        }
        result.append("    ]\n");
        
        result.append("  }\n}");
        return result.toString();
    }

    private String extractSpreadsheetData(Document document) {
        StringBuilder result = new StringBuilder();
        result.append("{\"spreadsheet\":{\"rows\":[");
        
        NodeList rows = document.getElementsByTagName("row");
        for (int i = 0; i < rows.getLength(); i++) {
            Node rowNode = rows.item(i);
            if (rowNode.getNodeType() == Node.ELEMENT_NODE) {
                Element rowElement = (Element) rowNode;
                result.append("{\"id\":\"").append(rowElement.getAttribute("id")).append("\",\"cells\":[");
                
                NodeList cells = rowElement.getElementsByTagName("cell");
                for (int j = 0; j < cells.getLength(); j++) {
                    Node cellNode = cells.item(j);
                    if (cellNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cellElement = (Element) cellNode;
                        result.append("{\"column\":\"").append(cellElement.getAttribute("column")).append("\",");
                        result.append("\"value\":\"").append(escapeJson(cellElement.getTextContent())).append("\"}");
                        if (j < cells.getLength() - 1) result.append(",");
                    }
                }
                result.append("]}");
                if (i < rows.getLength() - 1) result.append(",");
            }
        }
        result.append("]}}");
        return result.toString();
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
} 