package com.atlassian.spreadsheets.rest;

import com.atlassian.spreadsheets.api.SpreadsheetService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/")
@Produces({MediaType.APPLICATION_JSON})
public class SpreadsheetResource {

    private final SpreadsheetService spreadsheetService;

    public SpreadsheetResource(SpreadsheetService spreadsheetService) {
        this.spreadsheetService = spreadsheetService;
    }

    @POST
    @Path("/process-xml")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Response processXmlSpreadsheet(String xmlContent) {
        try {
            String result = spreadsheetService.processSpreadsheetXml(xmlContent);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Failed to process spreadsheet: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/upload")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, "application/vnd.ms-excel", "text/xml"})
    public Response uploadSpreadsheet(InputStream fileStream) {
        try {
            String result = spreadsheetService.processSpreadsheetFile(fileStream);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Failed to process uploaded file: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Alternative upload endpoint for testing (accepts raw XML in request body)
     * VULNERABLE ENDPOINT: Direct XML processing with XXE vulnerability 
     */
    @POST
    @Path("/import")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.TEXT_PLAIN})
    public Response importSpreadsheet(String xmlContent) {
        try {
            String result = spreadsheetService.processSpreadsheetXml(xmlContent);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Failed to import spreadsheet: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/validate")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Response validateSpreadsheet(String xmlContent) {
        try {
            boolean isValid = spreadsheetService.validateSpreadsheet(xmlContent);
            return Response.ok("{\"valid\": " + isValid + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Validation failed: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/info")
    public Response getInfo() {
        String info = "{\"supported_formats\": [\"XML\"], \"endpoints\": {\"process-xml\": \"POST XML content\"}}";
        return Response.ok(info).build();
    }
} 