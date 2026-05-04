package com.myoffice.payroll_system.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myoffice.payroll_system.entity.ShiftAssignment;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long> {
    List<ShiftAssignment> findByWorkDateBetween(LocalDate start, LocalDate end);
    List<ShiftAssignment> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate start, LocalDate end);
}
