package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.Budget;
import com.example.SmartSpendexpensetracker.model.Expense;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------- getBudgetsByUser() ----------------------
    @Test
    void testGetBudgetsByUser_ShouldReturnListOfBudgets() {
        String userEmail = "user@example.com";

        List<Budget> mockBudgets = new ArrayList<>();

        Budget budget1 = new Budget();
        budget1.setCategory("Food");
        budget1.setLimitAmount(1000.0);
        budget1.setUserEmail(userEmail);

        Budget budget2 = new Budget();
        budget2.setCategory("Travel");
        budget2.setLimitAmount(2000.0);
        budget2.setUserEmail(userEmail);

        mockBudgets.add(budget1);
        mockBudgets.add(budget2);

        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(mockBudgets);

        List<Budget> result = budgetService.getBudgetsByUser(userEmail);

        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getCategory());
        assertEquals("Travel", result.get(1).getCategory());
        verify(budgetRepository, times(1)).findByUserEmail(userEmail);
    }

    // ---------------------- saveBudget() ----------------------
    @Test
    void testSaveBudget_ShouldInvokeRepositorySave() {
        Budget budget = new Budget();
        budget.setCategory("Shopping");
        budget.setLimitAmount(1000.0);
        budget.setUserEmail("test@example.com");

        budgetService.saveBudget(budget);

        verify(budgetRepository, times(1)).save(budget);
    }

    // ---------------------- isBudgetExceeded() ----------------------
    @Test
    void testIsBudgetExceeded_ShouldReturnTrue_WhenExpensesExceedLimit() {
        String userEmail = "test@example.com";
        String category = "Food";

        Budget budget = new Budget();
        budget.setCategory(category);
        budget.setLimitAmount(1000.0);
        budget.setUserEmail(userEmail);

        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of(budget));

        Expense e1 = new Expense();
        e1.setCategory("Food");
        e1.setAmount(500.0);
        e1.setUserEmail(userEmail);

        Expense e2 = new Expense();
        e2.setCategory("Food");
        e2.setAmount(1000.0);
        e2.setUserEmail(userEmail);

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of(e1, e2));

        boolean result = budgetService.isBudgetExceeded(userEmail, category);

        assertTrue(result);
        verify(expenseRepository, times(1)).findByUserEmail(userEmail);
        verify(budgetRepository, times(1)).findByUserEmail(userEmail);
    }

    @Test
    void testIsBudgetExceeded_ShouldReturnFalse_WhenExpensesWithinLimit() {
        String userEmail = "test@example.com";
        String category = "Food";

        Budget budget = new Budget();
        budget.setCategory(category);
        budget.setLimitAmount(2000.0);
        budget.setUserEmail(userEmail);

        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of(budget));

        Expense e1 = new Expense();
        e1.setCategory("Food");
        e1.setAmount(500.0);
        e1.setUserEmail(userEmail);

        Expense e2 = new Expense();
        e2.setCategory("Food");
        e2.setAmount(1000.0);
        e2.setUserEmail(userEmail);

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of(e1, e2));

        boolean result = budgetService.isBudgetExceeded(userEmail, category);

        assertFalse(result);
    }

    @Test
    void testIsBudgetExceeded_ShouldHandleNullAmountsSafely() {
        String userEmail = "test@example.com";
        String category = "Food";

        Budget budget = new Budget();
        budget.setCategory(category);
        budget.setLimitAmount(500.0);
        budget.setUserEmail(userEmail);

        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of(budget));

        Expense e1 = new Expense();
        e1.setCategory("Food");
        e1.setAmount(null); // null amount
        e1.setUserEmail(userEmail);

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of(e1));

        boolean result = budgetService.isBudgetExceeded(userEmail, category);

        assertFalse(result); // total = 0, not exceeded
    }

    @Test
    void testIsBudgetExceeded_ShouldReturnFalse_WhenNoMatchingCategory() {
        String userEmail = "test@example.com";
        String category = "Entertainment";

        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setLimitAmount(1000.0);
        budget.setUserEmail(userEmail);

        when(budgetRepository.findByUserEmail(userEmail)).thenReturn(List.of(budget));

        Expense e1 = new Expense();
        e1.setCategory("Entertainment");
        e1.setAmount(500.0);
        e1.setUserEmail(userEmail);

        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of(e1));

        boolean result = budgetService.isBudgetExceeded(userEmail, category);

        assertFalse(result);
    }
}
