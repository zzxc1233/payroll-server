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

import com.myoffice.payroll_system.dto.PayrollDTO.PayrollRequest;
import com.myoffice.payroll_system.dto.PayrollDTO.PayrollResponse;
import com.myoffice.payroll_system.entity.PayrollStatus;
import com.myoffice.payroll_system.service.PayrollService;

@WebMvcTest(PayrollController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayrollService payrollService;

    @Test
    void getAllPayrollResponse_shouldReturnList() throws Exception {
        PayrollResponse response = new PayrollResponse();
        response.setId(1L);
        response.setEmployeeId(1L);
        response.setYear(2025);
        response.setMonth(5);
        response.setTotalAmount(new BigDecimal("50000"));
        response.setStatus(PayrollStatus.PAID);

        when(payrollService.getAllPayrollResponse()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/payrolls")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(50000));
    }

    @Test
    void getPayrollResponseById_shouldReturnPayroll() throws Exception {
        PayrollResponse response = new PayrollResponse();
        response.setId(1L);
        response.setEmployeeId(1L);

        when(payrollService.getPayrollResponseById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/payrolls/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeId").value(1));
    }

    @Test
    void createPayroll_shouldCreatePayroll() throws Exception {
        PayrollResponse response = new PayrollResponse();
        response.setId(1L);
        response.setTotalAmount(new BigDecimal("55000"));

        when(payrollService.createPayroll(any(PayrollRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/payrolls")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "employeeId": 1,
                            "year": 2025,
                            "month": 5,
                            "status": "PENDING",
                            "processedAt": "2025-05-09T10:00:00",
                            "totalAmount": 55000
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalAmount").value(55000));
    }

    @Test
    void deletePayroll_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/payrolls/{id}", 1L))
                .andExpect(status().isOk());
    }
}
