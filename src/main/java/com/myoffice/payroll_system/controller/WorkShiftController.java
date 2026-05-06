package com.myoffice.payroll_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myoffice.payroll_system.dto.WorkshiftDTO.WorkshiftRequest;
import com.myoffice.payroll_system.dto.WorkshiftDTO.WorkshiftResponse;
import com.myoffice.payroll_system.service.WorkShiftService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/workshifts")
@RequiredArgsConstructor
public class WorkShiftController {
    private final WorkShiftService workShiftService;

    @GetMapping
    public ResponseEntity<List<WorkshiftResponse>> getAllWorkShifts() {
        return ResponseEntity.ok(workShiftService.getAllWorkShifts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkshiftResponse> getWorkShiftById(@PathVariable Long id) {
        return ResponseEntity.ok(workShiftService.getWorkShiftById(id));
    }

    @PostMapping
    public ResponseEntity<WorkshiftResponse> createWorkShift(@Valid @RequestBody WorkshiftRequest request) {
        WorkshiftResponse workShift = workShiftService.createWorkShift(request);
        return ResponseEntity.ok(workShift);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkshiftResponse> updateWorkShift(@PathVariable Long id,
            @Valid @RequestBody WorkshiftRequest request) {
        WorkshiftResponse workShift = workShiftService.updateWorkShift(id, request);
        return ResponseEntity.ok(workShift);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkShift(@PathVariable Long id) {
        workShiftService.deleteWorkShift(id);
        return ResponseEntity.noContent().build();
    }
}
