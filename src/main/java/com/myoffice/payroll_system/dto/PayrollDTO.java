package com.myoffice.payroll_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.myoffice.payroll_system.entity.PayrollStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayrollDTO {
    @Data
    public static class PayrollRequest {
        private Long id;

        @NotNull
        private Long employeeId;

        @Min(value = 2020, message = "Year must be greater than or equal to 2020")
        private int year;

        @Min(value = 1, message = "Month must be greater than or equal to 1")
        @Max(value = 12, message = "Month must be less than or equal to 12")
        private int month;

        @NotNull
        private PayrollStatus status;

        @NotNull
        private LocalDateTime processedAt;

        @NotNull
        private BigDecimal totalAmount;
    }

    @Data
    public static class PayrollResponse {
        private Long id;
        private Long employeeId;
        private int year;
        private int month;
        private PayrollStatus status;
        private LocalDateTime processedAt;
        private BigDecimal totalAmount;
    }
}
