package com.myoffice.payroll_system.dto;

import java.math.BigDecimal;

import com.myoffice.payroll_system.entity.UserRole;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeDTO {
    @Data
    public static class EmployeeResponse {
        private Long id;
        private String fullName;
        private String position;
        private BigDecimal baseSalary;
        private String email;
        private UserRole role;
    }

    @Data
    public static class EmployeeRequest {
        private Long id;

        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Position is required")
        private String position;

        @NotNull(message = "Base salary is required")
        @DecimalMin(value = "0.01", message = "Base salary must be greater than 0")
        private BigDecimal baseSalary;

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        private String email;

        @NotNull(message = "Role is required")
        private UserRole role;
    }
}
