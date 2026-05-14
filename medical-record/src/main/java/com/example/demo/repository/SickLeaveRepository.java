package com.example.demo.repository;

import com.example.demo.model.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    @Query("SELECT MONTH(s.startDate) FROM SickLeave s GROUP BY MONTH(s.startDate) ORDER BY COUNT(s.id) DESC LIMIT 1")
    Integer findMonthWithMostSickLeaves();

    @Query("SELECT s.doctor FROM SickLeave s GROUP BY s.doctor ORDER BY COUNT(s.id) DESC LIMIT 1")
    com.example.demo.model.Doctor findDoctorWithMostSickLeaves();
}
