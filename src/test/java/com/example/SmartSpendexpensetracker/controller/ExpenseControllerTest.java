package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.exception.ResourceNotFoundException;
import com.example.SmartSpendexpensetracker.exception.UnauthorizedActionException;
import com.example.SmartSpendexpensetracker.model.Expense;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseControllerTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private ExpenseController expenseController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------- Test listExpenses --------------------
    @Test
    void testListExpenses_UserLoggedIn_ShouldReturnExpensesView() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        Expense expense1 = new Expense();
        expense1.setAmount(100.0);
        expense1.setUserEmail(userEmail);

        Expense expense2 = new Expense();
        expense2.setAmount(200.0);
        expense2.setUserEmail(userEmail);

        when(expenseRepository.findByUserEmail(userEmail))
                .thenReturn(List.of(expense1, expense2));

        String view = expenseController.listExpenses(session, model);

        assertEquals("expenses", view);
        verify(model).addAttribute(eq("expenses"), anyList());
        verify(model).addAttribute(eq("expense"), any(Expense.class));
        verify(model).addAttribute(eq("totalExpense"), eq(300.0));
    }

    @Test
    void testListExpenses_UserNotLoggedIn_ShouldThrowUnauthorized() {
        when(session.getAttribute("userEmail")).thenReturn(null);

        assertThrows(UnauthorizedActionException.class, () ->
                expenseController.listExpenses(session, model));
    }

    // -------------------- Test saveExpense --------------------
    @Test
    void testSaveExpense_UserLoggedIn_ShouldRedirect() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        Expense expense = new Expense();
        expense.setAmount(150.0);

        String view = expenseController.saveExpense(expense, session);

        assertEquals("redirect:/expenses", view);
        verify(expenseRepository).save(any(Expense.class));
        assertEquals(userEmail, expense.getUserEmail());
        assertNotNull(expense.getDate());
    }

    @Test
    void testSaveExpense_UserNotLoggedIn_ShouldThrowUnauthorized() {
        when(session.getAttribute("userEmail")).thenReturn(null);
        Expense expense = new Expense();

        assertThrows(UnauthorizedActionException.class, () ->
                expenseController.saveExpense(expense, session));
    }

    // -------------------- Test editExpense --------------------
    @Test
    void testEditExpense_ValidUser_ShouldReturnExpensesView() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUserEmail(userEmail);
        expense.setAmount(100.0);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(List.of(expense));

        String view = expenseController.editExpense(1L, model, session);

        assertEquals("expenses", view);
        verify(model).addAttribute(eq("expense"), eq(expense));
        verify(model).addAttribute(eq("expenses"), anyList());
        verify(model).addAttribute(eq("totalExpense"), eq(100.0));
    }

    @Test
    void testEditExpense_UnauthorizedUser_ShouldThrowUnauthorized() {
        String userEmail = "user1@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUserEmail("other@example.com");

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThrows(UnauthorizedActionException.class, () ->
                expenseController.editExpense(1L, model, session));
    }

    @Test
    void testEditExpense_NotFound_ShouldThrowResourceNotFound() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                expenseController.editExpense(1L, model, session));
    }

    // -------------------- Test deleteExpense --------------------
    @Test
    void testDeleteExpense_ValidUser_ShouldRedirect() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUserEmail(userEmail);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        String result = expenseController.deleteExpense(1L, session);

        assertEquals("redirect:/expenses", result);
        verify(expenseRepository).delete(expense);
    }

    @Test
    void testDeleteExpense_UnauthorizedUser_ShouldThrowUnauthorized() {
        String userEmail = "user1@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUserEmail("other@example.com");

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThrows(UnauthorizedActionException.class, () ->
                expenseController.deleteExpense(1L, session));
    }

    @Test
    void testDeleteExpense_NotFound_ShouldThrowResourceNotFound() {
        String userEmail = "test@example.com";
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                expenseController.deleteExpense(1L, session));
    }
}

