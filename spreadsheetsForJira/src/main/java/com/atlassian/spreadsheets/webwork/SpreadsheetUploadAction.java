package com.atlassian.spreadsheets.webwork;

import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.spreadsheets.api.SpreadsheetService;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

@SupportedMethods({RequestMethod.GET, RequestMethod.HEAD, RequestMethod.POST})
public class SpreadsheetUploadAction extends JiraWebActionSupport {

    private final SpreadsheetService spreadsheetService;
    private File uploadFile;
    private String uploadFileContentType;
    private String uploadFileFileName;
    private String result;
    private String error;

    public SpreadsheetUploadAction(SpreadsheetService spreadsheetService) {
        this.spreadsheetService = spreadsheetService;
    }

    @Override
    public String doDefault() throws Exception {
        return INPUT;
    }

    /**
     * Configure method for plugin configuration panel
     */
    public String doConfigure() throws Exception {
        return INPUT;
    }

    public String doUpload() throws Exception {
        if (uploadFile == null) {
            error = "Please select a file to upload";
            return INPUT;
        }

        try {
            // VULNERABLE: Process the uploaded file through the XXE vulnerable service
            FileInputStream fis = new FileInputStream(uploadFile);
            result = spreadsheetService.processSpreadsheetFile(fis);
            fis.close();
            
            return SUCCESS;
        } catch (Exception e) {
            error = "Error processing file: " + e.getMessage();
            return INPUT;
        }
    }

    public String doValidate() throws Exception {
        if (uploadFile == null) {
            error = "Please select a file to validate";
            return INPUT;
        }

        try {
            // VULNERABLE: Read and validate the file content
            String content = new String(Files.readAllBytes(uploadFile.toPath()));
            boolean isValid = spreadsheetService.validateSpreadsheet(content);
            result = "{\"valid\": " + isValid + ", \"message\": \"File validation " + (isValid ? "passed" : "failed") + "\"}";
            
            return SUCCESS;
        } catch (Exception e) {
            error = "Error validating file: " + e.getMessage();
            return INPUT;
        }
    }

    // Getters and setters for Struts file upload
    public File getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(File uploadFile) {
        this.uploadFile = uploadFile;
    }

    // Fallback setters for common multipart parameter names used by WebWork/Struts
    public void setFile(File file) {
        this.uploadFile = file;
    }

    public void setUpload(File upload) {
        this.uploadFile = upload;
    }

    public String getUploadFileContentType() {
        return uploadFileContentType;
    }

    public void setUploadFileContentType(String uploadFileContentType) {
        this.uploadFileContentType = uploadFileContentType;
    }

    public void setFileContentType(String contentType) {
        this.uploadFileContentType = contentType;
    }

    public void setUploadContentType(String contentType) {
        this.uploadFileContentType = contentType;
    }

    public String getUploadFileFileName() {
        return uploadFileFileName;
    }

    public void setUploadFileFileName(String uploadFileFileName) {
        this.uploadFileFileName = uploadFileFileName;
    }

    public void setFileFileName(String fileName) {
        this.uploadFileFileName = fileName;
    }

    public void setUploadFileName(String fileName) {
        this.uploadFileFileName = fileName;
    }

    public String getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    @Override
    public String doExecute() throws Exception {
        if (uploadFile == null) {
            error = "Please select a file to upload";
            return INPUT;
        }

        try {
            // VULNERABLE: Process the uploaded file through the XXE vulnerable service
            FileInputStream fis = new FileInputStream(uploadFile);
            result = spreadsheetService.processSpreadsheetFile(fis);
            fis.close();
            
            return SUCCESS;
        } catch (Exception e) {
            error = "Error processing file: " + e.getMessage();
            return INPUT;
        }
    }
} 