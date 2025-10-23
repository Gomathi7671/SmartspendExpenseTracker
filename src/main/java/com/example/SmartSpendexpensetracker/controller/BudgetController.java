package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.model.Budget;
import com.example.SmartSpendexpensetracker.repository.BudgetRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @GetMapping("/budget")
    public String showBudgets(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");

       if (userEmail == null) {
    return "redirect:/login";
}

        List<Budget> budgets = budgetRepository.findByUserEmail(userEmail);
        model.addAttribute("budgets", budgets);
        return "budget";
    }

    @PostMapping("/budget/add")
    public String addBudget(@RequestParam String category,
                            @RequestParam double limitAmount,
                            HttpSession session) {

        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login"; // ✅ Redirect if not logged in
        }

        Budget budget = new Budget();
        budget.setUserEmail(userEmail);
        budget.setCategory(category);
        budget.setLimitAmount(limitAmount);

        // ✅ Fix: store month name as String (e.g., "OCTOBER")
        budget.setMonth(LocalDate.now().getMonth().toString());

        budgetRepository.save(budget);

        return "redirect:/budget";
    }


    @GetMapping("/budget/delete/{id}")
public String deleteBudget(@PathVariable Long id, HttpSession session) {
    String userEmail = (String) session.getAttribute("email");
    if (userEmail == null) {
        return "redirect:/login";
    }

    budgetRepository.findById(id).ifPresent(budget -> {
        if (budget.getUserEmail().equals(userEmail)) {
            budgetRepository.delete(budget);
        }
    });

    return "redirect:/budget";
}

}
