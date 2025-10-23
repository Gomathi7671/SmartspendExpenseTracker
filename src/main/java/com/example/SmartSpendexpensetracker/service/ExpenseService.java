package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.Expense;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getExpensesForUser(String userEmail) {
        return expenseRepository.findByUserEmail(userEmail);
    }

    public void saveExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }


      // 🔹 NEW METHOD — fixes your error
    public double getTotalByCategory(String email, String category) {
        List<Expense> expenses = expenseRepository.findByUserEmailAndCategory(email, category);
        return expenses.stream()
                       .mapToDouble(Expense::getAmount)
                       .sum();
    }
}
