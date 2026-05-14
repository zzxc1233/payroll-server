package com.myoffice.payroll_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myoffice.payroll_system.config.SecurityAccessService;
import com.myoffice.payroll_system.dto.PayrollDTO.PayrollRequest;
import com.myoffice.payroll_system.dto.PayrollDTO.PayrollResponse;
import com.myoffice.payroll_system.service.PayrollService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;
    private final SecurityAccessService securityAccessService;

    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getAllPayrollResponse() {
        return ResponseEntity.ok(payrollService.getAllPayrollResponse());
    }

    @GetMapping("/me")
    @PreAuthorize("@securityAccessService.canAccessCurrentEmployeeOnly(authentication)")
    public ResponseEntity<List<PayrollResponse>> getCurrentEmployeePayrolls(Authentication authentication) {
        Long employeeId = securityAccessService.requireCurrentEmployeeId(authentication);
        return ResponseEntity.ok(payrollService.getPayrollResponsesByEmployeeId(employeeId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityAccessService.canAccessPayroll(authentication, @payrollService.getEmployeeIdForPayroll(#id))")
    public ResponseEntity<PayrollResponse> getPayrollResponseById(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getPayrollResponseById(id));
    }

    @PostMapping
    public ResponseEntity<PayrollResponse> createPayroll(@Valid @RequestBody PayrollRequest request) {
        PayrollResponse payroll = payrollService.createPayroll(request);
        return ResponseEntity.ok(payroll);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(@PathVariable Long id) {
        payrollService.deletePayroll(id);
        return ResponseEntity.noContent().build();
    }
}
