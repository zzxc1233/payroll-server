package com.myoffice.payroll_system.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ShiftAssignmentDTO {
    @Data
    public class ShiftAssignmentRequest {
        private Long id;
        private Long employeeId;
        private Long workShiftId;
        private LocalDate workDate;
        private String note;
    }

    @Data
    public class ShiftAssignmentResponse {
        private Long id;
        private Long employeeId;
        private Long workShiftId;
        private LocalDate workDate;
        private String note;
    }
}
