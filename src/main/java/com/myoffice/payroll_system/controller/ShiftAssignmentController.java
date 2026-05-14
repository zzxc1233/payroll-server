package com.myoffice.payroll_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myoffice.payroll_system.config.SecurityAccessService;
import com.myoffice.payroll_system.dto.ShiftAssignmentDTO.ShiftAssignmentRequest;
import com.myoffice.payroll_system.dto.ShiftAssignmentDTO.ShiftAssignmentResponse;
import com.myoffice.payroll_system.service.ShiftAssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shift-assignments")
@RequiredArgsConstructor
public class ShiftAssignmentController {
    private final ShiftAssignmentService shiftAssignmentService;
    private final SecurityAccessService securityAccessService;

    @GetMapping
    public ResponseEntity<List<ShiftAssignmentResponse>> getAllShiftAssignments() {
        return ResponseEntity.ok(shiftAssignmentService.getAllShiftAssignments());
    }

    @GetMapping("/me")
    @PreAuthorize("@securityAccessService.canAccessCurrentEmployeeOnly(authentication)")
    public ResponseEntity<List<ShiftAssignmentResponse>> getCurrentEmployeeShiftAssignments(Authentication authentication) {
        Long employeeId = securityAccessService.requireCurrentEmployeeId(authentication);
        return ResponseEntity.ok(shiftAssignmentService.getShiftAssignmentsByEmployeeId(employeeId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityAccessService.canAccessShiftAssignment(authentication, @shiftAssignmentService.getEmployeeIdForShiftAssignment(#id))")
    public ResponseEntity<ShiftAssignmentResponse> getShiftAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(shiftAssignmentService.getShiftAssignmentById(id));
    }

    @PostMapping
    public ResponseEntity<ShiftAssignmentResponse> createShiftAssignment(@Valid @RequestBody ShiftAssignmentRequest request) {
        ShiftAssignmentResponse shiftAssignment = shiftAssignmentService.createShiftAssignment(request);
        return ResponseEntity.ok(shiftAssignment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftAssignmentResponse> updateShiftAssignment(@PathVariable Long id, @Valid @RequestBody ShiftAssignmentRequest request) {
        ShiftAssignmentResponse shiftAssignment = shiftAssignmentService.updateShiftAssignment(id, request);
        return ResponseEntity.ok(shiftAssignment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShiftAssignment(@PathVariable Long id) {
        shiftAssignmentService.deleteShiftAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
