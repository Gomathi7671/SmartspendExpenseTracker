package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    // ✅ Add this route for report page
    @GetMapping("/report")
    public String reportPage(HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }
        return "report"; // This looks for templates/report.html
    }

    @GetMapping("/report/pdf")
    public ResponseEntity<InputStreamResource> downloadPDF(HttpSession session) throws Exception {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return ResponseEntity.badRequest().build();
        }

        var bis = reportService.generatePDFReport(userEmail);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/report/excel")
    public ResponseEntity<InputStreamResource> downloadExcel(HttpSession session) throws Exception {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return ResponseEntity.badRequest().build();
        }

        var bis = reportService.generateExcelReport(userEmail);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(bis));
    }
}
