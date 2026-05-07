package com.myoffice.payroll_system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myoffice.payroll_system.dto.WorkshiftDTO.WorkshiftRequest;
import com.myoffice.payroll_system.dto.WorkshiftDTO.WorkshiftResponse;
import com.myoffice.payroll_system.entity.WorkShift;
import com.myoffice.payroll_system.exception.ResourceNotFoundException;
import com.myoffice.payroll_system.repository.WorkShiftRepository;

@ExtendWith(MockitoExtension.class)
class WorkShiftServiceTest {

  @Mock
  private WorkShiftRepository workShiftRepository;

  @InjectMocks
  private WorkShiftService workShiftService;

  @Test
  void getAllWorkShift_shouldReturnAllWorkShift() {
    List<WorkShift> workShift = new ArrayList<>();
    WorkShift w1 = new WorkShift();
    w1.setId(1L);
    w1.setShiftName("Morning");
    w1.setStartTime(LocalTime.parse("09:00"));
    w1.setEndTime(LocalTime.parse("17:00"));
    w1.setExtraHourRate(new BigDecimal("1.5"));
    workShift.add(w1);

    WorkShift w2 = new WorkShift();
    w2.setId(2L);
    w2.setShiftName("Afternoon");
    w2.setStartTime(LocalTime.parse("13:00"));
    w2.setEndTime(LocalTime.parse("21:00"));
    w2.setExtraHourRate(new BigDecimal("1.2"));
    workShift.add(w2);

    when(workShiftRepository.findAll()).thenReturn(workShift);
    List<WorkshiftResponse> result = workShiftService.getAllWorkShifts();
    assertEquals(2, result.size());
    assertEquals("Morning", result.get(0).getShiftName());
    assertEquals("Afternoon", result.get(1).getShiftName());
  }

  @Test
  void getWorkShiftById_shouldReturnWorkShiftById() {
    WorkShift workShift = new WorkShift();
    workShift.setId(1L);
    workShift.setShiftName("Morning");
    workShift.setStartTime(LocalTime.parse("09:00"));
    workShift.setEndTime(LocalTime.parse("17:00"));
    workShift.setExtraHourRate(new BigDecimal("1.5"));

    when(workShiftRepository.findById(1L)).thenReturn(Optional.of(workShift));
    WorkshiftResponse result = workShiftService.getWorkShiftById(1L);
    assertEquals("Morning", result.getShiftName());
    assertEquals(LocalTime.parse("09:00"), result.getStartTime());
    assertEquals(LocalTime.parse("17:00"), result.getEndTime());
    assertEquals(new BigDecimal("1.5"), result.getExtraHourRate());
  }

  @Test
  void createWorkShift_shouldCreateWorkShift() {
    WorkshiftRequest request = new WorkshiftRequest();
    request.setShiftName("Morning");
    request.setStartTime(LocalTime.parse("09:00"));
    request.setEndTime(LocalTime.parse("17:00"));
    request.setExtraHourRate(new BigDecimal("1.5"));

    when(workShiftRepository.save(any(WorkShift.class))).thenAnswer(invocation -> {
      WorkShift ws = invocation.getArgument(0);
      ws.setId(1L);
      return ws;
    });

    WorkshiftResponse result = workShiftService.createWorkShift(request);
    assertEquals("Morning", result.getShiftName());
    assertEquals(LocalTime.parse("09:00"), result.getStartTime());
    assertEquals(LocalTime.parse("17:00"), result.getEndTime());
    assertEquals(new BigDecimal("1.5"), result.getExtraHourRate());
    assertEquals(1L, result.getId());
  }

  @Test
  void updateWorkShift_shouldUpdateWorkShift() {
    WorkShift existingWorkShift = new WorkShift();
    existingWorkShift.setId(1L);
    when(workShiftRepository.findById(1L)).thenReturn(Optional.of(existingWorkShift));
    when(workShiftRepository.save(any(WorkShift.class))).thenAnswer(invocation -> {
      WorkShift ws = invocation.getArgument(0);
      ws.setId(1L);
      return ws;
    });

    WorkshiftRequest request = new WorkshiftRequest();
    request.setShiftName("Morning");
    request.setStartTime(LocalTime.parse("09:00"));
    request.setEndTime(LocalTime.parse("17:00"));
    request.setExtraHourRate(new BigDecimal("1.5"));

    WorkshiftResponse result = workShiftService.updateWorkShift(1L, request);
    assertEquals(1L, result.getId());
    assertEquals("Morning", result.getShiftName());
    assertEquals(LocalTime.parse("09:00"), result.getStartTime());
    assertEquals(LocalTime.parse("17:00"), result.getEndTime());
    assertEquals(new BigDecimal("1.5"), result.getExtraHourRate());
  }

  @Test
  void updateWorkShift_shouldThrowResourceNotFoundException_whenWorkShiftNotFound() {
    when(workShiftRepository.findById(1L)).thenReturn(Optional.empty());
    WorkshiftRequest request = new WorkshiftRequest();
    request.setShiftName("Morning");
    request.setStartTime(LocalTime.parse("09:00"));
    request.setEndTime(LocalTime.parse("17:00"));
    request.setExtraHourRate(new BigDecimal("1.5"));

    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
        () -> workShiftService.updateWorkShift(1L, request));
    assertEquals("Work shift not found", ex.getMessage());
  }

  @Test
  void getWorkShiftById_shouldThrowResourceNotFoundException_whenWorkShiftNotFound() {
    when(workShiftRepository.findById(1L)).thenReturn(Optional.empty());
    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
        () -> workShiftService.getWorkShiftById(1L));
    assertEquals("Work shift not found", ex.getMessage());
  }

  @Test
  void deleteWorkShift_shouldDeleteWorkShift() {
    workShiftService.deleteWorkShift(1L);
    verify(workShiftRepository).deleteById(1L);
  }
}
