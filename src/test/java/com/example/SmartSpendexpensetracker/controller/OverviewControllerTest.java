package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.model.Budget;
import com.example.SmartSpendexpensetracker.model.Expense;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OverviewControllerTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private OverviewController overviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------- Test: User not logged in --------------------
    @Test
    void testShowOverview_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userEmail")).thenReturn(null);

        String view = overviewController.showOverview(session, model);

        assertEquals("redirect:/login", view);
    }

    // -------------------- Test: Valid user with income and expense --------------------
    @Test
    void testShowOverview_ValidUserWithData_ShouldReturnOverview() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        Expense income1 = new Expense();
        income1.setType("Income");
        income1.setAmount(1000.0);

        Expense expense1 = new Expense();
        expense1.setType("Expense");
        expense1.setAmount(400.0);

        Budget budget1 = new Budget();
        budget1.setLimitAmount(500.0);

        when(expenseRepository.findByUserEmail(userEmail))
                .thenReturn(List.of(income1, expense1));

        when(budgetRepository.findByUserEmail(userEmail))
                .thenReturn(List.of(budget1));

        String view = overviewController.showOverview(session, model);

        assertEquals("overview", view);
        verify(model).addAttribute(eq("overview"), any());
        verify(model).addAttribute(eq("notificationMsg"), anyString());
        verify(model).addAttribute(eq("notificationType"), anyString());
    }

    // -------------------- Test: No data --------------------
    @Test
    void testShowOverview_NoData_ShouldShowInfoMessage() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of());
        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of());

        String view = overviewController.showOverview(session, model);

        assertEquals("overview", view);
        verify(model).addAttribute("notificationMsg", "ℹ️ No data yet. Start adding income and expenses!");
        verify(model).addAttribute("notificationType", "info");
    }

    // -------------------- Test: Income equals expense --------------------
    @Test
    void testShowOverview_IncomeEqualsExpense_ShouldShowDangerMessage() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        Expense income = new Expense();
        income.setType("Income");
        income.setAmount(1000.0);

        Expense expense = new Expense();
        expense.setType("Expense");
        expense.setAmount(1000.0);

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of(income, expense));
        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of());

        String view = overviewController.showOverview(session, model);

        assertEquals("overview", view);
        verify(model).addAttribute("notificationMsg", "⚠️ You have no remaining balance. Income equals expenses!");
        verify(model).addAttribute("notificationType", "danger");
    }

    // -------------------- Test: Over 90% spending --------------------
    @Test
    void testShowOverview_Over90PercentSpent_ShouldShowDangerMessage() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        Expense income = new Expense();
        income.setType("Income");
        income.setAmount(1000.0);

        Expense expense = new Expense();
        expense.setType("Expense");
        expense.setAmount(950.0);

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of(income, expense));
        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of());

        overviewController.showOverview(session, model);

        verify(model).addAttribute("notificationMsg", "🚨 You’ve spent over 90% of your income!");
        verify(model).addAttribute("notificationType", "danger");
    }

    // -------------------- Test: Exception handling --------------------
    @Test
    void testShowOverview_ExceptionThrown_ShouldReturnErrorMessage() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(expenseRepository.findByUserEmail(userEmail))
                .thenThrow(new RuntimeException("DB error"));

        String view = overviewController.showOverview(session, model);

        assertEquals("overview", view);
        verify(model).addAttribute("notificationMsg", "❌ Error: Unable to load overview. Please try again later.");
        verify(model).addAttribute("notificationType", "danger");
    }
}
