package com.myoffice.payroll_system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myoffice.payroll_system.dto.EmployeeDTO;
import com.myoffice.payroll_system.dto.EmployeeDTO.EmployeeResponse;
import com.myoffice.payroll_system.entity.Employee;
import com.myoffice.payroll_system.exception.ResourceConflictException;
import com.myoffice.payroll_system.exception.ResourceNotFoundException;
import com.myoffice.payroll_system.repository.EmployeeRepository;
import com.myoffice.payroll_system.repository.PayrollRepository;
import com.myoffice.payroll_system.repository.ShiftAssignmentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;

    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
            .map(this::convertToResponse)
            .toList();
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return convertToResponse(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeDTO.EmployeeRequest employeeRequest) {
        Employee employee = new Employee();
        employee.setFullName(employeeRequest.getFullName());
        employee.setPosition(employeeRequest.getPosition());
        employee.setBaseSalary(employeeRequest.getBaseSalary());
        employee.setEmail(employeeRequest.getEmail());
        employee.setRole(employeeRequest.getRole());
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToResponse(savedEmployee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeDTO.EmployeeRequest employeeRequest) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        employee.setFullName(employeeRequest.getFullName());
        employee.setPosition(employeeRequest.getPosition());
        employee.setBaseSalary(employeeRequest.getBaseSalary());
        employee.setEmail(employeeRequest.getEmail());
        employee.setRole(employeeRequest.getRole());
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToResponse(savedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (payrollRepository.existsByEmployeeId(id)) {
            throw new ResourceConflictException("Employee cannot be deleted because payroll records still reference it.");
        }

        if (shiftAssignmentRepository.existsByEmployeeId(id)) {
            throw new ResourceConflictException("Employee cannot be deleted because shift assignments still reference it.");
        }

        employeeRepository.delete(employee);
    }

private EmployeeResponse convertToResponse(Employee employee) {
    EmployeeResponse response = new EmployeeResponse();
    response.setId(employee.getId());
    response.setFullName(employee.getFullName());
    response.setPosition(employee.getPosition());
    response.setBaseSalary(employee.getBaseSalary());
    response.setEmail(employee.getEmail());
    response.setRole(employee.getRole());
    return response;
}
}
