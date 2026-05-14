package com.myoffice.payroll_system.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ShiftAssignmentDTO {
    @Data
    public static class ShiftAssignmentRequest {
        private Long id;

        @NotNull
        @Positive(message = "Employee id must be greater than 0")
        private Long employeeId;

        @NotNull
        @Positive(message = "Work shift id must be greater than 0")
        private Long workShiftId;
        
        @NotNull
        private LocalDate workDate;
        
        private String note;
    }

    @Data
    public static class ShiftAssignmentResponse {
        private Long id;
        private Long employeeId;
        private Long workShiftId;
        private LocalDate workDate;
        private String note;
    }
}
