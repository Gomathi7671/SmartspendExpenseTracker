package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.exception.ResourceNotFoundException;
import com.example.SmartSpendexpensetracker.exception.UnauthorizedActionException;
import com.example.SmartSpendexpensetracker.model.Budget;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    // -------------------- Show Budgets --------------------
    @GetMapping("/budget")
    public String showBudgets(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            throw new UnauthorizedActionException("You must be logged in to view budgets.");
        }

        List<Budget> budgets = budgetRepository.findByUserEmail(userEmail);
        model.addAttribute("budgets", budgets);
        return "budget";
    }

    // -------------------- Add Budget --------------------
    @PostMapping("/budget/add")
    public String addBudget(@RequestParam String category,
                            @RequestParam Double limitAmount,
                            HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            throw new UnauthorizedActionException("You must be logged in to add a budget.");
        }

        Budget budget = new Budget();
        budget.setUserEmail(userEmail);
        budget.setCategory(category);
        budget.setLimitAmount(limitAmount);
        budget.setMonth(LocalDate.now().getMonth().toString());

        budgetRepository.save(budget);

        return "redirect:/budget";
    }

    // -------------------- Delete Budget --------------------
    @GetMapping("/budget/delete/{id}")
    public String deleteBudget(@PathVariable Long id, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            throw new UnauthorizedActionException("You must be logged in to delete a budget.");
        }

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with ID: " + id));

        if (!budget.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedActionException("You are not authorized to delete this budget.");
        }

        budgetRepository.delete(budget);

        return "redirect:/budget";
    }
}
