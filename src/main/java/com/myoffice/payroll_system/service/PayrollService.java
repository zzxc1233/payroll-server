package com.myoffice.payroll_system.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myoffice.payroll_system.entity.Employee;
import com.myoffice.payroll_system.entity.Payroll;
import com.myoffice.payroll_system.entity.ShiftAssignment;
import com.myoffice.payroll_system.entity.WorkShift;
import com.myoffice.payroll_system.repository.EmployeeRepository;
import com.myoffice.payroll_system.repository.PayrollRepository;
import com.myoffice.payroll_system.repository.ShiftAssignmentRepository;
import com.myoffice.payroll_system.repository.WorkShiftRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Payroll generateMonthlyPayroll(Long employeeId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<ShiftAssignment> shifts = shiftAssignmentRepository
                .findByEmployeeIdAndWorkDateBetween(employeeId, start, end);

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        BigDecimal totalAmount = calculateTotal(employee, shifts);
        
        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setTotalAmount(totalAmount);
        payroll.setMonth(month);
        payroll.setYear(year);
        
        return payrollRepository.save(payroll);
    }

    private BigDecimal calculateTotal(Employee employee, List<ShiftAssignment> shifts) {
        BigDecimal totalAmount = employee.getBaseSalary() != null ? employee.getBaseSalary() : BigDecimal.ZERO;

        if (shifts != null) {
            BigDecimal extraAmount = shifts.stream()
                    .filter(shift -> shift.getWorkShift() != null && shift.getWorkShift().getExtraHourRate() != null)
                    .map(shift -> shift.getWorkShift().getExtraHourRate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalAmount = totalAmount.add(extraAmount);
        }

        return totalAmount;
    }

    public List<Payroll> getPayrollHistory(int year, int month) {
        return payrollRepository.findByMonthAndYear(month, year);
    }
}
