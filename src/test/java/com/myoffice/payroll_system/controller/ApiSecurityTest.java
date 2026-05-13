package com.myoffice.payroll_system.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.myoffice.payroll_system.config.SecurityAccessService;
import com.myoffice.payroll_system.config.SecurityConfig;
import com.myoffice.payroll_system.dto.EmployeeDTO.EmployeeResponse;
import com.myoffice.payroll_system.dto.PayrollDTO.PayrollResponse;
import com.myoffice.payroll_system.dto.ShiftAssignmentDTO.ShiftAssignmentResponse;
import com.myoffice.payroll_system.entity.Employee;
import com.myoffice.payroll_system.repository.EmployeeRepository;
import com.myoffice.payroll_system.service.EmployeeService;
import com.myoffice.payroll_system.service.PayrollService;
import com.myoffice.payroll_system.service.ShiftAssignmentService;
import com.myoffice.payroll_system.service.WorkShiftService;

@WebMvcTest({ EmployeeController.class, PayrollController.class, ShiftAssignmentController.class, WorkShiftController.class })
@AutoConfigureMockMvc
@Import({ SecurityConfig.class, SecurityAccessService.class })
class ApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private PayrollService payrollService;

    @MockitoBean
    private ShiftAssignmentService shiftAssignmentService;

    @MockitoBean
    private WorkShiftService workShiftService;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Test
    void getEmployees_shouldReturnUnauthorized_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void getEmployees_shouldReturnForbidden_forEmployeeRole() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .with(jwt().jwt(jwt -> jwt.claim("employeeId", 1L))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void getCurrentEmployee_shouldAllowEmployeeToSeeOwnProfile() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setFullName("John Doe");
        when(employeeService.getEmployeeById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/employees/me")
                        .with(jwt().jwt(jwt -> jwt.claim("employeeId", 1L))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getCurrentEmployee_shouldAllowEmailBasedFallback_whenEmployeeIdClaimIsMissing() throws Exception {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmail("john@example.com");

        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setFullName("John Doe");

        when(employeeRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(employee));
        when(employeeService.getEmployeeById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/employees/me")
                        .with(jwt().jwt(jwt -> jwt.claim("email", "john@example.com"))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getEmployeeById_shouldReturnForbidden_whenEmployeeRequestsAnotherEmployee() throws Exception {
        when(employeeService.getEmployeeById(2L)).thenReturn(new EmployeeResponse());

        mockMvc.perform(get("/api/employees/{id}", 2L)
                        .with(jwt().jwt(jwt -> jwt.claim("employeeId", 1L))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void getEmployeeById_shouldAllowEmployeeToSeeOwnEmployeeRecord() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setFullName("John Doe");
        when(employeeService.getEmployeeById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/employees/{id}", 1L)
                        .with(jwt().jwt(jwt -> jwt.claim("employeeId", 1L))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getPayrolls_shouldReturnForbidden_forEmployeeRole() throws Exception {
        mockMvc.perform(get("/api/payrolls")
                        .with(jwt().jwt(jwt -> jwt.claim("employeeId", 1L))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCurrentEmployeePayrolls_shouldAllowEmployeeToSeeOwnPayrolls() throws Exception {
        PayrollResponse response = new PayrollResponse();
        response.setId(1L);
        response.setEmployeeId(1L);
        when(payrollService.getPayrollResponsesByEmployeeId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/payrolls/me")
                        .with(jwt().jwt(jwt -> jwt.claim("employeeId", 1L))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(1));
    }

    @Test
    void getCurrentEmployeePayrolls_shouldAllowEmailBasedFallback_whenEmployeeIdClaimIsMissing() throws Exception {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmail("john@example.com");

        PayrollResponse response = new PayrollResponse();
        response.setId(1L);
        response.setEmployeeId(1L);

        when(employeeRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(employee));
        when(payrollService.getPayrollResponsesByEmployeeId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/payrolls/me")
                        .with(jwt().jwt(jwt -> jwt.claim("email", "john@example.com"))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(1));
    }

    @Test
    void getShiftAssignments_shouldReturnForbidden_forEmployeeRole() throws Exception {
        mockMvc.perform(get("/api/shift-assignments")
                        .with(jwt().jwt(jwt -> jwt.claim("employeeId", 1L))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCurrentEmployeeShiftAssignments_shouldAllowEmployeeToSeeOwnAssignments() throws Exception {
        ShiftAssignmentResponse response = new ShiftAssignmentResponse();
        response.setId(1L);
        response.setEmployeeId(1L);
        when(shiftAssignmentService.getShiftAssignmentsByEmployeeId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/shift-assignments/me")
                        .with(jwt().jwt(jwt -> jwt.claim("employeeId", 1L))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(1));
    }

    @Test
    void getCurrentEmployeeShiftAssignments_shouldAllowEmailBasedFallback_whenEmployeeIdClaimIsMissing() throws Exception {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmail("john@example.com");

        ShiftAssignmentResponse response = new ShiftAssignmentResponse();
        response.setId(1L);
        response.setEmployeeId(1L);

        when(employeeRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(employee));
        when(shiftAssignmentService.getShiftAssignmentsByEmployeeId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/shift-assignments/me")
                        .with(jwt().jwt(jwt -> jwt.claim("email", "john@example.com"))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(1));
    }

    @Test
    void getCurrentEmployee_shouldReturnForbidden_whenTokenHasNoEmployeeIdOrMatchingEmail() throws Exception {
        when(employeeRepository.findByEmail("missing@example.com")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/employees/me")
                        .with(jwt().jwt(jwt -> jwt.claim("email", "missing@example.com"))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void getWorkShifts_shouldAllowEmployeeRole() throws Exception {
        mockMvc.perform(get("/api/workshifts")
                        .with(jwt().jwt(jwt -> jwt.claim("employeeId", 1L))
                                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isOk());
    }

    @Test
    void createEmployee_shouldReturnForbidden_forEmployeeRole() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fullName": "New Employee",
                                    "position": "Manager",
                                    "baseSalary": 75000,
                                    "email": "manager@example.com",
                                    "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void createEmployee_shouldAllowAdminRole() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setFullName("New Employee");

        when(employeeService.createEmployee(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/api/employees")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fullName": "New Employee",
                                    "position": "Manager",
                                    "baseSalary": 75000,
                                    "email": "manager@example.com",
                                    "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
