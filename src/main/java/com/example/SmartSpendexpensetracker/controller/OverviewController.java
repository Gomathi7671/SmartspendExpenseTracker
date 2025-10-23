package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.model.Overview;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class OverviewController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @GetMapping("/overview")
    public String showOverview(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }

        double totalIncome = expenseRepository.findByUserEmail(userEmail)
                .stream()
                .filter(e -> "Income".equalsIgnoreCase(e.getType()))
                .mapToDouble(e -> e.getAmount())
                .sum();

        double totalExpense = expenseRepository.findByUserEmail(userEmail)
                .stream()
                .filter(e -> "Expense".equalsIgnoreCase(e.getType()))
                .mapToDouble(e -> e.getAmount())
                .sum();

        double totalBudget = budgetRepository.findByUserEmail(userEmail)
                .stream()
                .mapToDouble(b -> b.getLimitAmount())
                .sum();

        double balance = totalIncome - totalExpense;

        model.addAttribute("overview", new Overview(totalIncome, totalExpense, totalBudget, balance));
        return "overview";
    }
}
