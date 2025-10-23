package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.Budget;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    // ✅ Get all budgets for a specific user
    public List<Budget> getBudgetsByUser(String userEmail) {
        return budgetRepository.findByUserEmail(userEmail);
    }

    // ✅ Save or update budget
    public void saveBudget(Budget budget) {
        budgetRepository.save(budget);
    }

    // ✅ Check if user exceeds budget (example logic)
    public boolean isBudgetExceeded(String userEmail, String category) {
        double totalExpenses = expenseRepository.findByUserEmail(userEmail).stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0)
                .sum();

        List<Budget> budgets = budgetRepository.findByUserEmail(userEmail);
        for (Budget b : budgets) {
            if (b.getCategory().equalsIgnoreCase(category) && totalExpenses > b.getLimitAmount()) {
                return true;
            }
        }
        return false;
    }
}
