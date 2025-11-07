package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.exception.ResourceNotFoundException;
import com.example.SmartSpendexpensetracker.exception.UnauthorizedActionException;
import com.example.SmartSpendexpensetracker.model.Budget;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetControllerTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private BudgetController budgetController;

    private Budget budget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        budget = new Budget();
        budget.setId(1L);
        budget.setCategory("Food");
        budget.setLimitAmount(5000.00);
        budget.setUserEmail("test@example.com");
        budget.setMonth(LocalDate.now().getMonth().toString());
    }

    // -------------------- Show Budgets --------------------
    @Test
    void testShowBudgetsAuthorized() {
        when(session.getAttribute("userEmail")).thenReturn("test@example.com");
        when(budgetRepository.findByUserEmail(eq("test@example.com")))
                .thenReturn(List.of(budget));

        String view = budgetController.showBudgets(session, model);

        assertEquals("budget", view);
        verify(budgetRepository).findByUserEmail("test@example.com");
        verify(model).addAttribute("budgets", List.of(budget));
    }

    @Test
    void testShowBudgetsUnauthorized() {
        when(session.getAttribute("userEmail")).thenReturn(null);

        assertThrows(UnauthorizedActionException.class,
                () -> budgetController.showBudgets(session, model));
    }

    // -------------------- Add Budget --------------------
    @Test
    void testAddBudgetAuthorized() {
        when(session.getAttribute("userEmail")).thenReturn("test@example.com");

        String view = budgetController.addBudget("Travel", 8000.0, session);

        assertEquals("redirect:/budget", view);
        ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
        verify(budgetRepository).save(captor.capture());

        Budget savedBudget = captor.getValue();
        assertEquals("Travel", savedBudget.getCategory());
        assertEquals(8000, savedBudget.getLimitAmount());
        assertEquals("test@example.com", savedBudget.getUserEmail());
        assertNotNull(savedBudget.getMonth());
    }

    @Test
    void testAddBudgetUnauthorized() {
        when(session.getAttribute("userEmail")).thenReturn(null);

        assertThrows(UnauthorizedActionException.class,
                () -> budgetController.addBudget("Food", 5000.0, session));
    }

    // -------------------- Delete Budget --------------------
    @Test
    void testDeleteBudgetAuthorized() {
        when(session.getAttribute("userEmail")).thenReturn("test@example.com");
        when(budgetRepository.findById(eq(1L))).thenReturn(Optional.of(budget));

        String view = budgetController.deleteBudget(1L, session);

        assertEquals("redirect:/budget", view);
        verify(budgetRepository).delete(budget);
    }

    @Test
    void testDeleteBudgetUnauthorizedSession() {
        when(session.getAttribute("userEmail")).thenReturn(null);

        assertThrows(UnauthorizedActionException.class,
                () -> budgetController.deleteBudget(1L, session));
    }

    @Test
    void testDeleteBudgetNotFound() {
        when(session.getAttribute("userEmail")).thenReturn("test@example.com");
        when(budgetRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> budgetController.deleteBudget(1L, session));
    }

    @Test
    void testDeleteBudgetUnauthorizedUser() {
        when(session.getAttribute("userEmail")).thenReturn("other@example.com");
        when(budgetRepository.findById(eq(1L))).thenReturn(Optional.of(budget));

        assertThrows(UnauthorizedActionException.class,
                () -> budgetController.deleteBudget(1L, session));

        verify(budgetRepository, never()).delete(any());
    }
}
