package com.example.SmartSpendexpensetracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String category;
    private Double limitAmount;
    private String month;
    private String description;

    private boolean alerted80 = false;
    private boolean alerted100 = false;

    public Budget() {}

    public Budget(Long id, String userEmail, String category, Double limitAmount, String month, String description) {
        this.id = id;
        this.userEmail = userEmail;
        this.category = category;
        this.limitAmount = limitAmount;
        this.month = month;
        this.description = description;
    }

    // ✅ Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(Double limitAmount) { this.limitAmount = limitAmount; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAlerted80() { return alerted80; }
    public void setAlerted80(boolean alerted80) { this.alerted80 = alerted80; }

    public boolean isAlerted100() { return alerted100; }
    public void setAlerted100(boolean alerted100) { this.alerted100 = alerted100; }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", category='" + category + '\'' +
                ", limitAmount=" + limitAmount +
                ", month='" + month + '\'' +
                ", description='" + description + '\'' +
                ", alerted80=" + alerted80 +
                ", alerted100=" + alerted100 +
                '}';
    }
}
