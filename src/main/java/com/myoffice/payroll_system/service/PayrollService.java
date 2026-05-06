package com.myoffice.payroll_system.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myoffice.payroll_system.dto.PayrollDTO.PayrollRequest;
import com.myoffice.payroll_system.dto.PayrollDTO.PayrollResponse;
import com.myoffice.payroll_system.entity.Employee;
import com.myoffice.payroll_system.entity.Payroll;
import com.myoffice.payroll_system.entity.PayrollStatus;
import com.myoffice.payroll_system.entity.ShiftAssignment;
import com.myoffice.payroll_system.exception.DuplicateResourceException;
import com.myoffice.payroll_system.exception.ResourceNotFoundException;
import com.myoffice.payroll_system.repository.EmployeeRepository;
import com.myoffice.payroll_system.repository.PayrollRepository;
import com.myoffice.payroll_system.repository.ShiftAssignmentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public PayrollResponse createPayroll(PayrollRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        payrollRepository.findByEmployeeIdAndMonthAndYear(request.getEmployeeId(), request.getMonth(), request.getYear())
                .ifPresent(p -> {
                    throw new DuplicateResourceException("Payroll for this employee in this month and year already exists.");
                });

        LocalDate startDate = LocalDate.of(request.getYear(), request.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<ShiftAssignment> assignments = shiftAssignmentRepository
            .findByEmployeeIdAndWorkDateBetween(request.getEmployeeId(), startDate, endDate);

        BigDecimal totalOT = BigDecimal.ZERO;

        for (ShiftAssignment assignment : assignments) {
            if (assignment.getWorkShift() != null) {
                totalOT = totalOT.add(assignment.getWorkShift().getExtraHourRate());
            }
        }

        BigDecimal totalAmount = employee.getBaseSalary().add(totalOT);

        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setYear(request.getYear());
        payroll.setMonth(request.getMonth());
        payroll.setStatus(PayrollStatus.PENDING);
        payroll.setTotalAmount(totalAmount);
        payroll.setProcessedAt(LocalDateTime.now());
        Payroll savedPayroll = payrollRepository.save(payroll);
        return convertToResponse(savedPayroll);
    }

    @Transactional
    public List<PayrollResponse> getAllPayrollResponse(){
        List<Payroll> payrolls = payrollRepository.findAll();
        return payrolls.stream().map(this::convertToResponse).toList();
    }

    @Transactional
    public PayrollResponse getPayrollResponseById(Long id){
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found"));
        return convertToResponse(payroll);
    }

    @Transactional
    public void deletePayroll(Long id) {
        payrollRepository.deleteById(id);
    }

    public PayrollResponse convertToResponse(Payroll payroll) {
        PayrollResponse response = new PayrollResponse();
        response.setId(payroll.getId());
        response.setEmployeeId(payroll.getEmployee().getId());
        response.setYear(payroll.getYear());
        response.setMonth(payroll.getMonth());
        response.setStatus(payroll.getStatus());
        response.setProcessedAt(payroll.getProcessedAt());
        response.setTotalAmount(payroll.getTotalAmount());
        return response;
    }
}
