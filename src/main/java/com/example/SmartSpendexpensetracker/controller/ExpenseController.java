package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.model.Expense;
import com.example.SmartSpendexpensetracker.repository.ExpenseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    // ✅ Display all expenses for logged-in user
    @GetMapping
    public String listExpenses(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }

        List<Expense> expenses = expenseRepository.findByUserEmail(userEmail);

        // ✅ Calculate total expense
        double totalExpense = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        model.addAttribute("expenses", expenses);
        model.addAttribute("expense", new Expense());
        model.addAttribute("totalExpense", totalExpense); // ✅ send to HTML

        return "expenses";
    }

    // ✅ Save new expense (CREATE)
    @PostMapping("/save")
    public String saveExpense(@ModelAttribute Expense expense, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";

        expense.setUserEmail(userEmail);
        if (expense.getDate() == null) expense.setDate(LocalDate.now());
        expenseRepository.save(expense);
        return "redirect:/expenses";
    }

    // ✅ Edit Expense (UPDATE)
    @GetMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id, Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        Expense expense = expenseRepository.findById(id).orElse(null);
        if (expense == null || !expense.getUserEmail().equals(userEmail)) {
            return "redirect:/expenses";
        }

        List<Expense> expenses = expenseRepository.findByUserEmail(userEmail);
        double totalExpense = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        model.addAttribute("expense", expense);
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalExpense", totalExpense);
        return "expenses";
    }

    // ✅ Delete Expense (DELETE)
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        Expense expense = expenseRepository.findById(id).orElse(null);
        if (expense != null && expense.getUserEmail().equals(userEmail)) {
            expenseRepository.delete(expense);
        }
        return "redirect:/expenses";
    }
}
