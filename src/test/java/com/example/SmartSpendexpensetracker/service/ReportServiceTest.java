package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.Budget;
import com.example.SmartSpendexpensetracker.model.Expense;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------- generatePDFReport ----------------------
    @Test
    void testGeneratePDFReport_ShouldReturnNonEmptyStream() throws DocumentException {
        String userEmail = "test@example.com";

        Expense expense = new Expense();
        expense.setTitle("Lunch");
        expense.setCategory("Food");
        expense.setType("Expense");
        expense.setAmount(500.0);
        expense.setDate(LocalDate.now());

        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setLimitAmount(1000.0);
        budget.setDescription("Monthly food budget");

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of(expense));
        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of(budget));

        ByteArrayInputStream pdfStream = reportService.generatePDFReport(userEmail);
        assertNotNull(pdfStream);
        assertTrue(pdfStream.available() > 0);

        verify(expenseRepository, times(1)).findByUserEmail(userEmail);
        verify(budgetRepository, times(1)).findByUserEmail(userEmail);
    }

    // ---------------------- generateExcelReport ----------------------
    @Test
    void testGenerateExcelReport_ShouldReturnNonEmptyStream() throws IOException {
        String userEmail = "test@example.com";

        Expense expense = new Expense();
        expense.setTitle("Lunch");
        expense.setCategory("Food");
        expense.setType("Expense");
        expense.setAmount(500.0);
        expense.setDate(LocalDate.now());

        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setLimitAmount(1000.0);
        budget.setDescription("Monthly food budget");

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of(expense));
        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of(budget));

        ByteArrayInputStream excelStream = reportService.generateExcelReport(userEmail);
        assertNotNull(excelStream);
        assertTrue(excelStream.available() > 0);

        verify(expenseRepository, times(1)).findByUserEmail(userEmail);
        verify(budgetRepository, times(1)).findByUserEmail(userEmail);
    }

    // ---------------------- Empty lists ----------------------
    @Test
    void testGeneratePDFReport_WithNoData_ShouldReturnStream() throws DocumentException {
        String userEmail = "test@example.com";

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of());
        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of());

        ByteArrayInputStream pdfStream = reportService.generatePDFReport(userEmail);
        assertNotNull(pdfStream);
        assertTrue(pdfStream.available() > 0);
    }

    @Test
    void testGenerateExcelReport_WithNoData_ShouldReturnStream() throws IOException {
        String userEmail = "test@example.com";

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of());
        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of());

        ByteArrayInputStream excelStream = reportService.generateExcelReport(userEmail);
        assertNotNull(excelStream);
        assertTrue(excelStream.available() > 0);
    }
}
