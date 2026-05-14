package com.myoffice.payroll_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.myoffice.payroll_system.config.SecurityAccessService;
import com.myoffice.payroll_system.dto.ShiftAssignmentDTO.ShiftAssignmentRequest;
import com.myoffice.payroll_system.dto.ShiftAssignmentDTO.ShiftAssignmentResponse;
import com.myoffice.payroll_system.exception.ResourceNotFoundException;
import com.myoffice.payroll_system.service.ShiftAssignmentService;

@WebMvcTest(ShiftAssignmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ShiftAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShiftAssignmentService shiftAssignmentService;

    @MockitoBean
    private SecurityAccessService securityAccessService;

    @Test
    void getAllShiftAssignments_shouldReturnList() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 5, 9);
        ShiftAssignmentResponse response = new ShiftAssignmentResponse();
        response.setId(1L);
        response.setEmployeeId(1L);
        response.setWorkShiftId(1L);
        response.setWorkDate(testDate);
        response.setNote("Note");

        when(shiftAssignmentService.getAllShiftAssignments()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/shift-assignments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].employeeId").value(1))
                .andExpect(jsonPath("$[0].workDate").value("2025-05-09"));
    }

    @Test
    void getShiftAssignmentById_shouldReturnById() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 5, 9);
        ShiftAssignmentResponse response = new ShiftAssignmentResponse();
        response.setId(1L);
        response.setEmployeeId(1L);
        response.setWorkShiftId(1L);
        response.setWorkDate(testDate);

        when(shiftAssignmentService.getEmployeeIdForShiftAssignment(1L)).thenReturn(1L);
        when(securityAccessService.canAccessShiftAssignment(any(), any(Long.class))).thenReturn(true);
        when(shiftAssignmentService.getShiftAssignmentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/shift-assignments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeId").value(1))
                .andExpect(jsonPath("$.workDate").value("2025-05-09"));
    }

    @Test
    void createShiftAssignment_shouldCreateShiftAssignment() throws Exception {
        ShiftAssignmentRequest request = new ShiftAssignmentRequest();
        request.setEmployeeId(1L);
        request.setWorkShiftId(1L);
        request.setWorkDate(LocalDate.now());

        ShiftAssignmentResponse response = new ShiftAssignmentResponse();
        response.setId(1L);
        response.setEmployeeId(1L);
        response.setWorkShiftId(1L);
        response.setWorkDate(LocalDate.now());

        when(shiftAssignmentService.createShiftAssignment(any(ShiftAssignmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/shift-assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "employeeId":1,
                            "workShiftId":1,
                            "workDate":"2025-01-01"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeId").value(1));
    }

    @Test
    void updateShiftAssignment_shouldUpdateShiftAssignment() throws Exception {
        ShiftAssignmentRequest request = new ShiftAssignmentRequest();
        request.setEmployeeId(1L);
        request.setWorkShiftId(2L);
        request.setWorkDate(LocalDate.now());

        ShiftAssignmentResponse response = new ShiftAssignmentResponse();
        response.setId(1L);
        response.setWorkShiftId(2L);

        when(shiftAssignmentService.updateShiftAssignment(any(Long.class), any(ShiftAssignmentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/shift-assignments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "employeeId":1,
                            "workShiftId":2,
                            "workDate":"2025-01-01"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.workShiftId").value(2));
    }

    @Test
    void deleteShiftAssignment_shouldDeleteShiftAssignment() throws Exception {
        mockMvc.perform(delete("/api/shift-assignments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getShiftAssignmentById_shouldThrowResourceNotFoundException_whenShiftAssignmentNotFound() throws Exception {
        when(shiftAssignmentService.getEmployeeIdForShiftAssignment(1L)).thenReturn(1L);
        when(securityAccessService.canAccessShiftAssignment(any(), any(Long.class))).thenReturn(true);
        when(shiftAssignmentService.getShiftAssignmentById(1L))
                .thenThrow(new ResourceNotFoundException("Shift assignment not found"));

        mockMvc.perform(get("/api/shift-assignments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
