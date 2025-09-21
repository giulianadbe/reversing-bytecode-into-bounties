package com.atlassian.spreadsheets.api;

import java.io.InputStream;

public interface SpreadsheetService {
    
    /**
     * Process an XML-based spreadsheet file
     * @param xmlContent The XML content of the spreadsheet
     * @return Processed spreadsheet data as JSON string
     */
    String processSpreadsheetXml(String xmlContent) throws Exception;
    
    /**
     * Process an uploaded spreadsheet file stream
     * @param inputStream The input stream of the uploaded file
     * @return Processed spreadsheet data
     */
    String processSpreadsheetFile(InputStream inputStream) throws Exception;
    
    /**
     * Validate spreadsheet data structure
     * @param xmlContent The XML content to validate
     * @return true if valid, false otherwise
     */
    boolean validateSpreadsheet(String xmlContent);
} 