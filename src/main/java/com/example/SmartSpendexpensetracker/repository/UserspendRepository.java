package com.example.SmartSpendexpensetracker.repository;



import com.example.SmartSpendexpensetracker.model.UserSpend;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserspendRepository extends JpaRepository<UserSpend, Long> {
    Optional<UserSpend> findByEmail(String email);
}
