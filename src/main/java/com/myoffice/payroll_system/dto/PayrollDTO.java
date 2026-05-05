package com.myoffice.payroll_system.dto;

import java.time.LocalDateTime;
import com.myoffice.payroll_system.entity.PayrollStatus;

import lombok.Data;

@Data
public class PayrollDTO {
    @Data
    public class PayrollRequest {
        private Long id;
        private Long employeeId;
        private int year;
        private int month;
        private PayrollStatus status;
        private LocalDateTime processedAt;
    }

    @Data
    public class PayrollResponse {
        private Long id;
        private Long employeeId;
        private int year;
        private int month;
        private PayrollStatus status;
        private LocalDateTime processedAt;
    }
}
