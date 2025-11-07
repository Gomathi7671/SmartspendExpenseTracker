package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.Expense;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------- getExpensesForUser ----------------------
    @Test
    void testGetExpensesForUser_ShouldReturnList() {
        String email = "user@example.com";

        Expense e1 = new Expense();
        e1.setAmount(100.0);
        Expense e2 = new Expense();
        e2.setAmount(200.0);

        when(expenseRepository.findByUserEmail(email)).thenReturn(List.of(e1, e2));

        List<Expense> expenses = expenseService.getExpensesForUser(email);

        assertEquals(2, expenses.size());
        verify(expenseRepository, times(1)).findByUserEmail(email);
    }

    // ---------------------- saveExpense ----------------------
    @Test
    void testSaveExpense_ShouldCallRepositorySave() {
        Expense expense = new Expense();
        expense.setAmount(150.0);

        expenseService.saveExpense(expense);

        verify(expenseRepository, times(1)).save(expense);
    }

    // ---------------------- getExpenseById ----------------------
    @Test
    void testGetExpenseById_ShouldReturnOptionalExpense() {
        Expense expense = new Expense();
        expense.setAmount(300.0);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        Optional<Expense> result = expenseService.getExpenseById(1L);

        assertTrue(result.isPresent());
        assertEquals(300.0, result.get().getAmount());
        verify(expenseRepository, times(1)).findById(1L);
    }

    // ---------------------- deleteExpense ----------------------
    @Test
    void testDeleteExpense_ShouldCallRepositoryDeleteById() {
        Long id = 1L;

        expenseService.deleteExpense(id);

        verify(expenseRepository, times(1)).deleteById(id);
    }

    // ---------------------- getTotalByCategory ----------------------
    @Test
    void testGetTotalByCategory_ShouldReturnSumOfAmounts() {
        String email = "user@example.com";
        String category = "Food";

        Expense e1 = new Expense();
        e1.setAmount(100.0);
        Expense e2 = new Expense();
        e2.setAmount(250.0);

        when(expenseRepository.findByUserEmailAndCategory(email, category)).thenReturn(List.of(e1, e2));

        double total = expenseService.getTotalByCategory(email, category);

        assertEquals(350.0, total);
        verify(expenseRepository, times(1)).findByUserEmailAndCategory(email, category);
    }

    @Test
    void testGetTotalByCategory_ShouldReturnZero_WhenNoExpenses() {
        String email = "user@example.com";
        String category = "Transport";

        when(expenseRepository.findByUserEmailAndCategory(email, category)).thenReturn(List.of());

        double total = expenseService.getTotalByCategory(email, category);

        assertEquals(0.0, total);
        verify(expenseRepository, times(1)).findByUserEmailAndCategory(email, category);
    }
}
