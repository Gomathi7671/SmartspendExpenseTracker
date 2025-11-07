package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------- Test reportPage --------------------
    @Test
    void testReportPage_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userEmail")).thenReturn(null);

        String view = reportController.reportPage(session);

        assertEquals("redirect:/login", view);
    }

    @Test
    void testReportPage_UserLoggedIn_ShouldReturnReportPage() {
        when(session.getAttribute("userEmail")).thenReturn("test@example.com");

        String view = reportController.reportPage(session);

        assertEquals("report", view);
    }

    // -------------------- Test downloadPDF --------------------
    @Test
    void testDownloadPDF_UserNotLoggedIn_ShouldReturnBadRequest() {
        when(session.getAttribute("userEmail")).thenReturn(null);

        ResponseEntity<InputStreamResource> response = reportController.downloadPDF(session);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testDownloadPDF_ValidUser_ShouldReturnPDFResponse() throws Exception {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        ByteArrayInputStream bis = new ByteArrayInputStream("PDF data".getBytes());
        when(reportService.generatePDFReport(userEmail)).thenReturn(bis);

        ResponseEntity<InputStreamResource> response = reportController.downloadPDF(session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("report.pdf"));
        assertNotNull(response.getBody());
    }

    @Test
    void testDownloadPDF_ExceptionThrown_ShouldReturnInternalServerError() throws Exception {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(reportService.generatePDFReport(userEmail)).thenThrow(new RuntimeException("PDF error"));

        ResponseEntity<InputStreamResource> response = reportController.downloadPDF(session);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals(MediaType.TEXT_PLAIN, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }

    // -------------------- Test downloadExcel --------------------
    @Test
    void testDownloadExcel_UserNotLoggedIn_ShouldReturnBadRequest() {
        when(session.getAttribute("userEmail")).thenReturn(null);

        ResponseEntity<InputStreamResource> response = reportController.downloadExcel(session);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testDownloadExcel_ValidUser_ShouldReturnExcelResponse() throws Exception {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        ByteArrayInputStream bis = new ByteArrayInputStream("Excel data".getBytes());
        when(reportService.generateExcelReport(userEmail)).thenReturn(bis);

        ResponseEntity<InputStreamResource> response = reportController.downloadExcel(session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("application/vnd.ms-excel", response.getHeaders().getContentType().toString());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("report.xlsx"));
        assertNotNull(response.getBody());
    }

    @Test
    void testDownloadExcel_ExceptionThrown_ShouldReturnInternalServerError() throws Exception {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(reportService.generateExcelReport(userEmail)).thenThrow(new RuntimeException("Excel error"));

        ResponseEntity<InputStreamResource> response = reportController.downloadExcel(session);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals(MediaType.TEXT_PLAIN, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }
}
