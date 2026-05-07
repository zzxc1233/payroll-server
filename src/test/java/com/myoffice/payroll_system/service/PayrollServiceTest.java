package com.myoffice.payroll_system.service;

import com.myoffice.payroll_system.dto.PayrollDTO.PayrollRequest;
import com.myoffice.payroll_system.dto.PayrollDTO.PayrollResponse;
import com.myoffice.payroll_system.entity.Employee;
import com.myoffice.payroll_system.entity.Payroll;
import com.myoffice.payroll_system.entity.PayrollStatus;
import com.myoffice.payroll_system.entity.ShiftAssignment;
import com.myoffice.payroll_system.entity.WorkShift;
import com.myoffice.payroll_system.exception.DuplicateResourceException;
import com.myoffice.payroll_system.exception.ResourceNotFoundException;
import com.myoffice.payroll_system.repository.EmployeeRepository;
import com.myoffice.payroll_system.repository.PayrollRepository;
import com.myoffice.payroll_system.repository.ShiftAssignmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

  @Mock
  private PayrollRepository payrollRepository;

  @Mock
  private ShiftAssignmentRepository shiftAssignmentRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private PayrollService payrollService;

  @Test
  void createPayroll_shouldCalculateTotalAmountCorrectly() {

    PayrollRequest request = new PayrollRequest();
    request.setEmployeeId(1L);
    request.setYear(2026);
    request.setMonth(5);

    Employee employee = new Employee();
    employee.setId(1L);
    employee.setBaseSalary(new BigDecimal("1000.00"));

    WorkShift shift = new WorkShift();
    shift.setExtraHourRate(new BigDecimal("200.00"));

    ShiftAssignment assignment = new ShiftAssignment();
    assignment.setWorkShift(shift);

    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2026))
        .thenReturn(Optional.empty());
    when(shiftAssignmentRepository.findByEmployeeIdAndWorkDateBetween(
        any(Long.class), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of(assignment));

    when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> {
      Payroll p = invocation.getArgument(0);
      p.setId(10L);
      return p;
    });

    PayrollResponse result = payrollService.createPayroll(request);

    assertNotNull(result);
    assertEquals(new BigDecimal("1200.00"), result.getTotalAmount());
    assertEquals(1L, result.getEmployeeId());
    assertEquals(2026, result.getYear());
    assertEquals(5, result.getMonth());
    assertEquals(PayrollStatus.PENDING, result.getStatus());
  }

  @Test
  void createPayroll_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
    PayrollRequest request = new PayrollRequest();
    request.setEmployeeId(999L);
    request.setYear(2026);
    request.setMonth(5);

    when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
        () -> payrollService.createPayroll(request));
    assertEquals("Employee not found", ex.getMessage());
  }

  @Test
  void createPayroll_shouldThrowDuplicateResourceException_whenPayrollAlreadyExists() {
    PayrollRequest request = new PayrollRequest();
    request.setEmployeeId(1L);
    request.setYear(2026);
    request.setMonth(5);

    Employee employee = new Employee();
    employee.setId(1L);
    employee.setBaseSalary(new BigDecimal("1000.00"));

    Payroll existing = new Payroll();
    existing.setId(111L);

    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2026)).thenReturn(Optional.of(existing));

    DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
        () -> payrollService.createPayroll(request));
    assertEquals("Payroll for this employee in this month and year already exists.", ex.getMessage());
  }

  @Test
  void createPayroll_totalAmountShouldBeEqualToBaseSalaryIfNoShiftAssignments() {
    PayrollRequest request = new PayrollRequest();
    request.setEmployeeId(1L);
    request.setYear(2026);
    request.setMonth(5);

    Employee employee = new Employee();
    employee.setId(1L);
    employee.setBaseSalary(new BigDecimal("1000.00"));

    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2026)).thenReturn(Optional.empty());
    when(shiftAssignmentRepository.findByEmployeeIdAndWorkDateBetween(
        any(Long.class), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of());

    when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> {
      Payroll p = invocation.getArgument(0);
      p.setId(10L);
      return p;
    });

    PayrollResponse result = payrollService.createPayroll(request);

    assertNotNull(result);
    assertEquals(new BigDecimal("1000.00"), result.getTotalAmount());
    assertEquals(1L, result.getEmployeeId());
    assertEquals(2026, result.getYear());
    assertEquals(5, result.getMonth());
    assertEquals(PayrollStatus.PENDING, result.getStatus());
  }

  @Test
  void deletePayroll_shouldDeletePayroll() {
    payrollService.deletePayroll(1L);
    verify(payrollRepository).deleteById(1L);
  }
}