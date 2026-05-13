package com.myoffice.payroll_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.myoffice.payroll_system.config.SecurityAccessService;
import com.myoffice.payroll_system.dto.EmployeeDTO;
import com.myoffice.payroll_system.dto.EmployeeDTO.EmployeeResponse;
import com.myoffice.payroll_system.entity.UserRole;
import com.myoffice.payroll_system.service.EmployeeService;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private SecurityAccessService securityAccessService;

    @Test
    void getAllEmployees_shouldReturnList() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setFullName("John Doe");
        response.setPosition("Developer");
        response.setBaseSalary(new BigDecimal("50000"));
        response.setEmail("john@example.com");
        response.setRole(UserRole.EMPLOYEE);

        when(employeeService.getAllEmployees()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setFullName("John Doe");

        when(employeeService.getEmployeeById(1L)).thenReturn(response);
        when(securityAccessService.canAccessEmployee(any(), any(Long.class))).thenReturn(true);

        mockMvc.perform(get("/api/employees/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void createEmployee_shouldCreateEmployee() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setFullName("New Employee");

        when(employeeService.createEmployee(any(EmployeeDTO.EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/employees")
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("New Employee"));
    }

    @Test
    void updateEmployee_shouldUpdateEmployee() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setFullName("Updated Name");

        when(employeeService.updateEmployee(any(Long.class), any(EmployeeDTO.EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/employees/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "fullName": "Updated Name",
                            "position": "Senior Manager",
                            "baseSalary": 85000,
                            "email": "senior@example.com",
                            "role": "ADMIN"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Updated Name"));
    }

    @Test
    void deleteEmployee_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/employees/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
