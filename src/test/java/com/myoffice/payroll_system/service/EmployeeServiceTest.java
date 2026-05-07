package com.myoffice.payroll_system.service;

import com.myoffice.payroll_system.entity.Employee;
import com.myoffice.payroll_system.repository.EmployeeRepository;
import com.myoffice.payroll_system.entity.UserRole;
import com.myoffice.payroll_system.dto.EmployeeDTO.EmployeeResponse;
import com.myoffice.payroll_system.dto.EmployeeDTO.EmployeeRequest;
import com.myoffice.payroll_system.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private EmployeeService employeeService;

  @Test
  void getAllEmployees_shouldReturnAllEmployees() {
    List<Employee> employees = new ArrayList<>();
    Employee e1 = new Employee();
    e1.setId(1L);
    e1.setFullName("John Doe");
    e1.setPosition("Software Engineer");
    e1.setBaseSalary(new BigDecimal("100000"));
    e1.setEmail("john.doe@example.com");
    e1.setRole(UserRole.EMPLOYEE);
    employees.add(e1);

    Employee e2 = new Employee();
    e2.setId(2L);
    e2.setFullName("Jane Smith");
    e2.setPosition("Product Manager");
    e2.setBaseSalary(new BigDecimal("120000"));
    e2.setEmail("jane.smith@example.com");
    e2.setRole(UserRole.ADMIN);
    employees.add(e2);

    when(employeeRepository.findAll()).thenReturn(employees);
    List<EmployeeResponse> result = employeeService.getAllEmployees();

    assertEquals(2, result.size());
    assertEquals("John Doe", result.get(0).getFullName());
    assertEquals("Jane Smith", result.get(1).getFullName());
  }

  @Test
  void getEmployeeById_shouldReturnEmployeeById() {
    Employee employee = new Employee();
    employee.setId(1L);
    employee.setFullName("John Doe");
    employee.setPosition("Software Engineer");
    employee.setBaseSalary(new BigDecimal("100000"));
    employee.setEmail("john.doe@example.com");
    employee.setRole(UserRole.EMPLOYEE);

    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    EmployeeResponse result = employeeService.getEmployeeById(1L);
    assertEquals("John Doe", result.getFullName());
    assertEquals("Software Engineer", result.getPosition());
    assertEquals(new BigDecimal("100000"), result.getBaseSalary());
    assertEquals("john.doe@example.com", result.getEmail());
    assertEquals(UserRole.EMPLOYEE, result.getRole());
  }

  @Test
  void createEmployee_shouldCreateEmployee() {
    EmployeeRequest request = new EmployeeRequest();
    request.setFullName("John Doe");
    request.setPosition("Software Engineer");
    request.setBaseSalary(new BigDecimal("100000"));
    request.setEmail("john.doe@example.com");
    request.setRole(UserRole.EMPLOYEE);

    when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
      Employee e = invocation.getArgument(0);
      e.setId(10L);
      return e;
    });

    EmployeeResponse result = employeeService.createEmployee(request);
    assertEquals("John Doe", result.getFullName());
    assertEquals("Software Engineer", result.getPosition());
    assertEquals(new BigDecimal("100000"), result.getBaseSalary());
    assertEquals("john.doe@example.com", result.getEmail());
    assertEquals(UserRole.EMPLOYEE, result.getRole());
    assertEquals(10L, result.getId());
  }

  @Test
  void updateEmployee_shouldUpdateEmployee() {
    Employee existingEmployee = new Employee();
    existingEmployee.setId(1L);
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));
    when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
      Employee e = invocation.getArgument(0);
      e.setId(1L);
      return e;
    });

    EmployeeRequest request = new EmployeeRequest();
    request.setFullName("John Doe");
    request.setPosition("Software Engineer");
    request.setBaseSalary(new BigDecimal("100000"));
    request.setEmail("john.doe@example.com");
    request.setRole(UserRole.EMPLOYEE);

    EmployeeResponse result = employeeService.updateEmployee(1L, request);

    assertEquals(1L, result.getId());
    assertEquals("John Doe", result.getFullName());
    assertEquals("Software Engineer", result.getPosition());
    assertEquals(new BigDecimal("100000"), result.getBaseSalary());
    assertEquals("john.doe@example.com", result.getEmail());
    assertEquals(UserRole.EMPLOYEE, result.getRole());
  }

  @Test
  void getAllEmployees_shouldReturnEmptyList_whenNoEmployees() {
    when(employeeRepository.findAll()).thenReturn(List.of());
    List<EmployeeResponse> result = employeeService.getAllEmployees();
    assertEquals(0, result.size());
  }

  @Test
  void getEmployeeById_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
        () -> employeeService.getEmployeeById(1L));
    assertEquals("Employee not found", ex.getMessage());
  }

  @Test
  void deleteEmployee_shouldDeleteEmployee() {
    employeeService.deleteEmployee(1L);
    verify(employeeRepository).deleteById(1L);
  }
}