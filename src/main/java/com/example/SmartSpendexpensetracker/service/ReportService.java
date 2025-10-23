package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.Expense;
import com.example.SmartSpendexpensetracker.model.Budget;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    // 📄 Generate PDF report
    public ByteArrayInputStream generatePDFReport(String userEmail) throws DocumentException {
        List<Expense> expenses = expenseRepository.findByUserEmail(userEmail);
        List<Budget> budgets = budgetRepository.findByUserEmail(userEmail);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("SmartSpend Expense Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("User: " + userEmail));
        document.add(Chunk.NEWLINE);

        // Expenses Table
        PdfPTable expenseTable = new PdfPTable(5);
        expenseTable.setWidthPercentage(100);
        expenseTable.addCell("Title");
        expenseTable.addCell("Category");
        expenseTable.addCell("Type");
        expenseTable.addCell("Amount");
        expenseTable.addCell("Date");

        for (Expense e : expenses) {
            expenseTable.addCell(e.getTitle());
            expenseTable.addCell(e.getCategory());
            expenseTable.addCell(e.getType());
            expenseTable.addCell(String.valueOf(e.getAmount()));
            expenseTable.addCell(String.valueOf(e.getDate()));
        }

        document.add(expenseTable);
        document.add(Chunk.NEWLINE);

        // Budgets Table
        PdfPTable budgetTable = new PdfPTable(3);
        budgetTable.setWidthPercentage(100);
        budgetTable.addCell("Category");
        budgetTable.addCell("Limit Amount");
        budgetTable.addCell("Description");

        for (Budget b : budgets) {
            budgetTable.addCell(b.getCategory());
            budgetTable.addCell(String.valueOf(b.getLimitAmount()));
            budgetTable.addCell(b.getDescription());
        }

        document.add(budgetTable);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    // 📊 Generate Excel report
    public ByteArrayInputStream generateExcelReport(String userEmail) throws IOException {
        List<Expense> expenses = expenseRepository.findByUserEmail(userEmail);
        List<Budget> budgets = budgetRepository.findByUserEmail(userEmail);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet expenseSheet = workbook.createSheet("Expenses");
            Sheet budgetSheet = workbook.createSheet("Budgets");

            // Expense Sheet
            Row headerRow = expenseSheet.createRow(0);
            String[] expenseCols = {"Title", "Category", "Type", "Amount", "Date"};
            for (int i = 0; i < expenseCols.length; i++) {
                headerRow.createCell(i).setCellValue(expenseCols[i]);
            }

            int rowIdx = 1;
            for (Expense e : expenses) {
                Row row = expenseSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getTitle());
                row.createCell(1).setCellValue(e.getCategory());
                row.createCell(2).setCellValue(e.getType());
                row.createCell(3).setCellValue(e.getAmount());
                row.createCell(4).setCellValue(e.getDate().toString());
            }

            // Budget Sheet
            Row headerRow2 = budgetSheet.createRow(0);
            String[] budgetCols = {"Category", "Limit Amount", "Description"};
            for (int i = 0; i < budgetCols.length; i++) {
                headerRow2.createCell(i).setCellValue(budgetCols[i]);
            }

            int rowIdx2 = 1;
            for (Budget b : budgets) {
                Row row = budgetSheet.createRow(rowIdx2++);
                row.createCell(0).setCellValue(b.getCategory());
                row.createCell(1).setCellValue(b.getLimitAmount());
                row.createCell(2).setCellValue(b.getDescription());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
