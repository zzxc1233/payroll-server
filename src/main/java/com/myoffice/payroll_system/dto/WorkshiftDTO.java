package com.myoffice.payroll_system.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkshiftDTO {
    @Data
    public static class WorkshiftRequest {
        private Long id;

        @NotBlank(message = "Shift name is required")
        private String shiftName;

        @NotNull(message = "Start time is required")
        private LocalTime startTime;

        @NotNull(message = "End time is required")
        private LocalTime endTime;

        @NotNull(message = "Extra hour rate is required")
        private BigDecimal extraHourRate;
    }

    @Data
    public static class WorkshiftResponse {
        private Long id;
        private String shiftName;
        private LocalTime startTime;
        private LocalTime endTime;
        private BigDecimal extraHourRate;
    }
}
