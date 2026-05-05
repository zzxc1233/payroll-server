package com.myoffice.payroll_system.dto;

import java.math.BigDecimal;

import com.myoffice.payroll_system.entity.UserRole;

import lombok.Data;

@Data
public class EmployeeDTO {
    @Data
    public class EmployeeResponse {
        private Long id;
        private String fullName;
        private String position;
        private BigDecimal baseSalary;
        private String email;
        private UserRole role;
    }

    @Data
    public class EmployeeRequest {
        private Long id;
        private String fullName;
        private String position;
        private BigDecimal baseSalary;
        private String email;
        private UserRole role;
    }
}