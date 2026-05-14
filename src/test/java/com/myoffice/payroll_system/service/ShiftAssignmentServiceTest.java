package com.myoffice.payroll_system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myoffice.payroll_system.dto.ShiftAssignmentDTO;
import com.myoffice.payroll_system.dto.ShiftAssignmentDTO.ShiftAssignmentRequest;
import com.myoffice.payroll_system.entity.Employee;
import com.myoffice.payroll_system.entity.ShiftAssignment;
import com.myoffice.payroll_system.entity.WorkShift;
import com.myoffice.payroll_system.exception.DuplicateResourceException;
import com.myoffice.payroll_system.exception.ResourceNotFoundException;
import com.myoffice.payroll_system.repository.EmployeeRepository;
import com.myoffice.payroll_system.repository.ShiftAssignmentRepository;
import com.myoffice.payroll_system.repository.WorkShiftRepository;

@ExtendWith(MockitoExtension.class)
public class ShiftAssignmentServiceTest {

    @Mock
    private ShiftAssignmentRepository shiftAssignmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private WorkShiftRepository workShiftRepository;

    @InjectMocks
    private ShiftAssignmentService shiftAssignmentService;

    @Test
    void getAllShiftAssignments_shouldReturnAllShiftAssignments() {
        ShiftAssignment shiftAssignment = new ShiftAssignment();
        shiftAssignment.setId(1L);
        shiftAssignment.setNote("Test Shift");

        Employee employee = new Employee();
        employee.setId(1L);
        shiftAssignment.setEmployee(employee);

        WorkShift workShift = new WorkShift();
        workShift.setId(1L);
        workShift.setStartTime(LocalTime.parse("08:00"));
        workShift.setEndTime(LocalTime.parse("17:00"));
        shiftAssignment.setWorkShift(workShift);

        List<ShiftAssignment> shiftAssignments = Arrays.asList(shiftAssignment);
        when(shiftAssignmentRepository.findAll()).thenReturn(shiftAssignments);

        List<ShiftAssignmentDTO.ShiftAssignmentResponse> result = shiftAssignmentService.getAllShiftAssignments();

        assertEquals(1, result.size());
        assertEquals("Test Shift", result.get(0).getNote());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals(1L, result.get(0).getWorkShiftId());
        verify(shiftAssignmentRepository).findAll();
    }

    @Test
    void getShiftAssignmentById_shouldReturnShiftAssignmentById() {
        ShiftAssignment shiftAssignment = new ShiftAssignment();
        shiftAssignment.setId(1L);
        shiftAssignment.setNote("Test Shift");

        Employee employee = new Employee();
        employee.setId(1L);
        shiftAssignment.setEmployee(employee);

        WorkShift workShift = new WorkShift();
        workShift.setId(1L);
        workShift.setStartTime(LocalTime.parse("08:00"));
        workShift.setEndTime(LocalTime.parse("17:00"));
        shiftAssignment.setWorkShift(workShift);

        when(shiftAssignmentRepository.findById(1L)).thenReturn(Optional.of(shiftAssignment));
        ShiftAssignmentDTO.ShiftAssignmentResponse result = shiftAssignmentService.getShiftAssignmentById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Test Shift", result.getNote());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(1L, result.getWorkShiftId());
        verify(shiftAssignmentRepository).findById(1L);
    }

    @Test
    void createShiftAssignment_shouldCreateShiftAssignment() {
        ShiftAssignmentDTO.ShiftAssignmentRequest request = new ShiftAssignmentDTO.ShiftAssignmentRequest();
        request.setEmployeeId(1L);
        request.setWorkShiftId(1L);
        request.setWorkDate(LocalDate.of(2024, 5, 6));
        request.setNote("Test Shift");

        Employee employee = new Employee();
        employee.setId(1L);

        WorkShift workShift = new WorkShift();
        workShift.setId(1L);
        workShift.setStartTime(LocalTime.parse("08:00"));
        workShift.setEndTime(LocalTime.parse("17:00"));

        ShiftAssignment shiftAssignment = new ShiftAssignment();
        shiftAssignment.setId(1L);
        shiftAssignment.setNote("Test Shift");
        shiftAssignment.setEmployee(employee);
        shiftAssignment.setWorkShift(workShift);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(workShiftRepository.findById(1L)).thenReturn(Optional.of(workShift));
        when(shiftAssignmentRepository.existsByEmployeeIdAndWorkDate(1L, LocalDate.of(2024, 5, 6))).thenReturn(false);
        when(shiftAssignmentRepository.save(any(ShiftAssignment.class))).thenReturn(shiftAssignment);

        ShiftAssignmentDTO.ShiftAssignmentResponse result = shiftAssignmentService.createShiftAssignment(request);

        assertEquals(1L, result.getId());
        assertEquals("Test Shift", result.getNote());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(1L, result.getWorkShiftId());
        verify(employeeRepository).findById(1L);
        verify(workShiftRepository).findById(1L);
        verify(shiftAssignmentRepository).save(any(ShiftAssignment.class));
    }

    @Test
    void updateShiftAssignment_shouldUpdateShiftAssignment() {
        ShiftAssignmentRequest request = new ShiftAssignmentRequest();
        request.setEmployeeId(1L);
        request.setWorkShiftId(1L);
        request.setWorkDate(LocalDate.of(2024, 5, 7));
        request.setNote("Updated Shift");

        Employee employee = new Employee();
        employee.setId(1L);

        WorkShift workShift = new WorkShift();
        workShift.setId(1L);
        workShift.setStartTime(LocalTime.parse("08:00"));
        workShift.setEndTime(LocalTime.parse("17:00"));

        ShiftAssignment existingShiftAssignment = new ShiftAssignment();
        existingShiftAssignment.setId(1L);
        existingShiftAssignment.setNote("Test Shift");
        existingShiftAssignment.setEmployee(employee);
        existingShiftAssignment.setWorkShift(workShift);

        when(shiftAssignmentRepository.findById(1L)).thenReturn(Optional.of(existingShiftAssignment));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(workShiftRepository.findById(1L)).thenReturn(Optional.of(workShift));
        when(shiftAssignmentRepository.existsByEmployeeIdAndWorkDateAndIdNot(1L, LocalDate.of(2024, 5, 7), 1L)).thenReturn(false);
        when(shiftAssignmentRepository.save(any(ShiftAssignment.class))).thenReturn(existingShiftAssignment);

        ShiftAssignmentDTO.ShiftAssignmentResponse result = shiftAssignmentService.updateShiftAssignment(1L, request);

        assertEquals(1L, result.getId());
        assertEquals("Updated Shift", result.getNote());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(1L, result.getWorkShiftId());
        verify(shiftAssignmentRepository).findById(1L);
        verify(employeeRepository).findById(1L);
        verify(workShiftRepository).findById(1L);
        verify(shiftAssignmentRepository).save(any(ShiftAssignment.class));
    }

    @Test
    void deleteShiftAssignment_shouldDeleteShiftAssignment() {
        ShiftAssignment sa = new ShiftAssignment();
        sa.setId(1L);
        when(shiftAssignmentRepository.findById(1L)).thenReturn(Optional.of(sa));
        shiftAssignmentService.deleteShiftAssignment(1L);
        verify(shiftAssignmentRepository).delete(sa);
    }

    @Test
    void getShiftAssignmentById_shouldThrowResourceNotFoundException_whenShiftAssignmentNotFound() {
        when(shiftAssignmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> shiftAssignmentService.getShiftAssignmentById(1L));
    }

    @Test
    void createShiftAssignment_shouldThrowDuplicateResourceException_whenEmployeeAlreadyHasAssignmentOnDate() {
        ShiftAssignmentRequest request = new ShiftAssignmentRequest();
        request.setEmployeeId(1L);
        request.setWorkShiftId(1L);
        request.setWorkDate(LocalDate.of(2024, 5, 6));

        when(shiftAssignmentRepository.existsByEmployeeIdAndWorkDate(1L, LocalDate.of(2024, 5, 6))).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> shiftAssignmentService.createShiftAssignment(request));
    }

    @Test
    void updateShiftAssignment_shouldThrowDuplicateResourceException_whenEmployeeAlreadyHasAnotherAssignmentOnDate() {
        ShiftAssignmentRequest request = new ShiftAssignmentRequest();
        request.setEmployeeId(1L);
        request.setWorkShiftId(1L);
        request.setWorkDate(LocalDate.of(2024, 5, 6));

        when(shiftAssignmentRepository.existsByEmployeeIdAndWorkDateAndIdNot(1L, LocalDate.of(2024, 5, 6), 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> shiftAssignmentService.updateShiftAssignment(1L, request));
    }
}
