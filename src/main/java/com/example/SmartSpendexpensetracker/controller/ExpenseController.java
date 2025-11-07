package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.exception.ResourceNotFoundException;
import com.example.SmartSpendexpensetracker.exception.UnauthorizedActionException;
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

    // -------------------- List all expenses --------------------
    @GetMapping
    public String listExpenses(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new UnauthorizedActionException("You must be logged in to view expenses.");

        List<Expense> expenses = expenseRepository.findByUserEmail(userEmail);

        double totalExpense = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        model.addAttribute("expenses", expenses);
        model.addAttribute("expense", new Expense());
        model.addAttribute("totalExpense", totalExpense);

        return "expenses";
    }

    // -------------------- Save new expense --------------------
    @PostMapping("/save")
    public String saveExpense(@ModelAttribute Expense expense, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new UnauthorizedActionException("You must be logged in to add an expense.");

        expense.setUserEmail(userEmail);
        if (expense.getDate() == null) expense.setDate(LocalDate.now());
        expenseRepository.save(expense);

        return "redirect:/expenses";
    }

    // -------------------- Edit Expense --------------------
    @GetMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id, Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new UnauthorizedActionException("You must be logged in to edit an expense.");

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + id));

        if (!expense.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedActionException("You are not authorized to edit this expense.");
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

    // -------------------- Delete Expense --------------------
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new UnauthorizedActionException("You must be logged in to delete an expense.");

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + id));

        if (!expense.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedActionException("You are not authorized to delete this expense.");
        }

        expenseRepository.delete(expense);
        return "redirect:/expenses";
    }
}
