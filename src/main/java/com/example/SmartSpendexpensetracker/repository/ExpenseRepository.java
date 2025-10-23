package com.example.SmartSpendexpensetracker.repository;

import com.example.SmartSpendexpensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // ✅ Get all expenses for a specific user
    List<Expense> findByUserEmail(String userEmail);

    // ✅ Get all expenses for a user and a specific category
    List<Expense> findByUserEmailAndCategory(String userEmail, String category);
}
