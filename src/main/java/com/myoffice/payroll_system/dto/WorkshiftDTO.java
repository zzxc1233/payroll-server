package com.myoffice.payroll_system.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.Data;

@Data
public class WorkshiftDTO {
    @Data
    public class WorkshiftRequest {
        private Long id;
        private String name;
        private LocalTime startTime;
        private LocalTime endTime;
        private BigDecimal extraHourRate;
    }

    @Data
    public class WorkshiftResponse {
        private Long id;
        private String name;
        private LocalTime startTime;
        private LocalTime endTime;
        private BigDecimal extraHourRate;
    }
}
