package com.example.SmartSpendexpensetracker.model;

public class Overview {

    private double totalIncome;
    private double totalExpense;
    private double totalBudget;
    private double balance;

    public Overview(double totalIncome, double totalExpense, double totalBudget, double balance) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.totalBudget = totalBudget;
        this.balance = balance;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public double getBalance() {
        return balance;
    }
}
