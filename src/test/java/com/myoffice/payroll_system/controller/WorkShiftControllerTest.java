package com.myoffice.payroll_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import com.myoffice.payroll_system.exception.ResourceNotFoundException;
import com.myoffice.payroll_system.dto.WorkshiftDTO.WorkshiftRequest;
import com.myoffice.payroll_system.dto.WorkshiftDTO.WorkshiftResponse;
import com.myoffice.payroll_system.service.WorkShiftService;

@WebMvcTest(WorkShiftController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WorkShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkShiftService workShiftService;

    @Test
    void getAllWorkShifts_shouldReturnList() throws Exception {
        WorkshiftResponse response = new WorkshiftResponse();
        response.setId(1L);
        response.setShiftName("Morning");

        when(workShiftService.getAllWorkShifts()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/workshifts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shiftName").value("Morning"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getWorkShiftById_shouldReturnById() throws Exception {
        WorkshiftResponse response = new WorkshiftResponse();
        response.setId(1L);
        response.setShiftName("Morning");

        when(workShiftService.getWorkShiftById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/workshifts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shiftName").value("Morning"));
    }

    @Test
    void createWorkShift_shouldCreateWorkShift() throws Exception {
        WorkshiftRequest request = new WorkshiftRequest();
        request.setShiftName("Morning");
        request.setStartTime(LocalTime.parse("09:00"));
        request.setEndTime(LocalTime.parse("17:00"));
        request.setExtraHourRate(new BigDecimal("1.5"));

        WorkshiftResponse response = new WorkshiftResponse();
        response.setId(1L);
        response.setShiftName("Morning");
        response.setStartTime(LocalTime.parse("09:00"));
        response.setEndTime(LocalTime.parse("17:00"));
        response.setExtraHourRate(new BigDecimal("1.5"));

        when(workShiftService.createWorkShift(any(WorkshiftRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/workshifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "shiftName":"Morning",
                            "startTime":"09:00:00",
                            "endTime":"17:00:00",
                            "extraHourRate":1.5
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shiftName").value("Morning"));
    }

    @Test
    void updateWorkShift_shouldUpdateWorkShift() throws Exception {
        WorkshiftRequest request = new WorkshiftRequest();
        request.setShiftName("Morning");
        request.setStartTime(LocalTime.parse("09:00"));
        request.setEndTime(LocalTime.parse("17:00"));
        request.setExtraHourRate(new BigDecimal("1.5"));

        WorkshiftResponse response = new WorkshiftResponse();
        response.setId(1L);
        response.setShiftName("Evening");

        when(workShiftService.updateWorkShift(any(Long.class), any(WorkshiftRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/workshifts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "shiftName":"Morning",
                            "startTime":"09:00:00",
                            "endTime":"17:00:00",
                            "extraHourRate":1.5
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shiftName").value("Evening"));
    }

    @Test
    void deleteWorkShift_shouldDeleteWorkShift() throws Exception {
        mockMvc.perform(delete("/api/workshifts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getWorkShiftById_shouldThrowResourceNotFoundException_whenWorkShiftNotFound() throws Exception {
        when(workShiftService.getWorkShiftById(1L)).thenThrow(new ResourceNotFoundException("Work shift not found"));
        mockMvc.perform(get("/api/workshifts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Work shift not found"));
    }

    @Test
    void updateWorkShift_shouldThrowResourceNotFoundException_whenWorkShiftNotFound() throws Exception {
        when(workShiftService.updateWorkShift(any(Long.class), any(WorkshiftRequest.class)))
                .thenThrow(new ResourceNotFoundException("Work shift not found"));
        mockMvc.perform(put("/api/workshifts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "shiftName":"Morning",
                            "startTime":"09:00:00",
                            "endTime":"17:00:00",
                            "extraHourRate":1.5
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Work shift not found"));
    }

    @Test
    void createWorkShift_shouldReturnBadRequest_whenShiftNameIsEmpty() throws Exception {
        mockMvc.perform(post("/api/workshifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "shiftName":"",
                            "startTime":"",
                            "endTime":"",
                            "extraHourRate":null
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.shiftName").value("Shift name is required"))
                .andExpect(jsonPath("$.startTime").value("Start time is required"))
                .andExpect(jsonPath("$.endTime").value("End time is required"))
                .andExpect(jsonPath("$.extraHourRate").value("Extra hour rate is required"));
    }

    @Test
    void updateWorkShift_shouldReturnBadRequest_whenShiftNameIsEmpty() throws Exception {
        mockMvc.perform(put("/api/workshifts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "shiftName":"",
                            "startTime":"",
                            "endTime":"",
                            "extraHourRate":null
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.shiftName").value("Shift name is required"))
                .andExpect(jsonPath("$.startTime").value("Start time is required"))
                .andExpect(jsonPath("$.endTime").value("End time is required"))
                .andExpect(jsonPath("$.extraHourRate").value("Extra hour rate is required"));
    }
}
