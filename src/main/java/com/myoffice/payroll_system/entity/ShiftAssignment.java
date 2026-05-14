package com.myoffice.payroll_system.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(
    name = "shift_assignments",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_shift_assignments_employee_work_date",
        columnNames = {"employee_id", "work_date"}
    ),
    indexes = {
        @Index(name = "idx_shift_assignments_employee_id", columnList = "employee_id"),
        @Index(name = "idx_shift_assignments_work_date", columnList = "work_date")
    }
)
@Data
public class ShiftAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "shift_id", nullable = false)
    private WorkShift workShift;

    @Column(nullable = false)
    private LocalDate workDate;

    private String note;
}
