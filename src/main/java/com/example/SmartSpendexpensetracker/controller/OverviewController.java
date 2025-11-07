package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.model.Overview;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OverviewController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @GetMapping("/overview")
    public String showOverview(HttpSession session, Model model) {
        try {
            // ✅ Check if user is logged in
            String userEmail = (String) session.getAttribute("userEmail");
            if (userEmail == null) {
                return "redirect:/login";
            }

            // ✅ Calculate total income
            double totalIncome = expenseRepository.findByUserEmail(userEmail)
                    .stream()
                    .filter(e -> "Income".equalsIgnoreCase(e.getType()))
                    .mapToDouble(e -> e.getAmount())
                    .sum();

            // ✅ Calculate total expense
            double totalExpense = expenseRepository.findByUserEmail(userEmail)
                    .stream()
                    .filter(e -> "Expense".equalsIgnoreCase(e.getType()))
                    .mapToDouble(e -> e.getAmount())
                    .sum();

            // ✅ Calculate total budget
            double totalBudget = budgetRepository.findByUserEmail(userEmail)
                    .stream()
                    .mapToDouble(b -> b.getLimitAmount())
                    .sum();

            // ✅ Calculate balance
            double balance = totalIncome - totalExpense;

            // ✅ Add overview to model
            model.addAttribute("overview", new Overview(totalIncome, totalExpense, totalBudget, balance));

            // ✅ Enhanced notification logic
            if (totalIncome == 0 && totalExpense == 0) {
                model.addAttribute("notificationMsg", "ℹ️ No data yet. Start adding income and expenses!");
                model.addAttribute("notificationType", "info");
            } else if (totalIncome == totalExpense && totalIncome > 0) {
                model.addAttribute("notificationMsg", "⚠️ You have no remaining balance. Income equals expenses!");
                model.addAttribute("notificationType", "danger");
            } else if (totalIncome > 0) {
                double percentageUsed = (totalExpense / totalIncome) * 100;

                if (percentageUsed >= 90) {
                    model.addAttribute("notificationMsg", "🚨 You’ve spent over 90% of your income!");
                    model.addAttribute("notificationType", "danger");
                } else if (percentageUsed >= 75) {
                    model.addAttribute("notificationMsg", "⚠️ Caution: You’ve spent about 75% of your income.");
                    model.addAttribute("notificationType", "warning");
                } else if (percentageUsed >= 50) {
                    model.addAttribute("notificationMsg", "💡 Heads up: You’ve used about half of your income.");
                    model.addAttribute("notificationType", "info");
                } else {
                    model.addAttribute("notificationMsg", "✅ Great job! You’re spending wisely.");
                    model.addAttribute("notificationType", "success");
                }
            }

            return "overview"; // Thymeleaf template: overview.html

        } catch (Exception e) {
            // ✅ Global exception handling for this controller
            model.addAttribute("notificationMsg", "❌ Error: Unable to load overview. Please try again later.");
            model.addAttribute("notificationType", "danger");
            return "overview";
        }
    }
}
