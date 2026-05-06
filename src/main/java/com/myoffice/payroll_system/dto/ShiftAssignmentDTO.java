package com.myoffice.payroll_system.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShiftAssignmentDTO {
    @Data
    public static class ShiftAssignmentRequest {
        private Long id;

        @NotNull
        private Long employeeId;

        @NotNull
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
