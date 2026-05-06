package com.myoffice.payroll_system.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myoffice.payroll_system.dto.ShiftAssignmentDTO.ShiftAssignmentRequest;
import com.myoffice.payroll_system.dto.ShiftAssignmentDTO.ShiftAssignmentResponse;
import com.myoffice.payroll_system.entity.ShiftAssignment;
import com.myoffice.payroll_system.exception.ResourceNotFoundException;
import com.myoffice.payroll_system.repository.EmployeeRepository;
import com.myoffice.payroll_system.repository.ShiftAssignmentRepository;
import com.myoffice.payroll_system.repository.WorkShiftRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftAssignmentService {
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkShiftRepository workShiftRepository;

    public List<ShiftAssignmentResponse> getAllShiftAssignments() {
        List<ShiftAssignment> shiftAssignments = shiftAssignmentRepository.findAll();
        return shiftAssignments.stream()
            .map(this::convertToResponse)
            .toList();
    }

    public ShiftAssignmentResponse getShiftAssignmentById(Long id) {
        ShiftAssignment shiftAssignment = shiftAssignmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shift assignment not found"));
        return convertToResponse(shiftAssignment);
    }

    @Transactional
    public ShiftAssignmentResponse createShiftAssignment(ShiftAssignmentRequest request) {
        ShiftAssignment shiftAssignment = new ShiftAssignment();
        shiftAssignment.setEmployee(employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found")));
        shiftAssignment.setWorkShift(workShiftRepository.findById(request.getWorkShiftId())
            .orElseThrow(() -> new ResourceNotFoundException("Work shift not found")));
        shiftAssignment.setWorkDate(request.getWorkDate());
        shiftAssignment.setNote(request.getNote());
        ShiftAssignment savedShiftAssignment = shiftAssignmentRepository.save(shiftAssignment);
        return convertToResponse(savedShiftAssignment);
    }
    
    @Transactional
    public ShiftAssignmentResponse updateShiftAssignment(Long id, ShiftAssignmentRequest request) {
        ShiftAssignment shiftAssignment = shiftAssignmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shift assignment not found"));
        shiftAssignment.setEmployee(employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found")));
        shiftAssignment.setWorkShift(workShiftRepository.findById(request.getWorkShiftId())
            .orElseThrow(() -> new ResourceNotFoundException("Work shift not found")));
        shiftAssignment.setWorkDate(request.getWorkDate());
        shiftAssignment.setNote(request.getNote());
        ShiftAssignment savedShiftAssignment = shiftAssignmentRepository.save(shiftAssignment);
        return convertToResponse(savedShiftAssignment);
    }

    @Transactional
    public void deleteShiftAssignment(Long id) {
        ShiftAssignment shiftAssignment = shiftAssignmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shift assignment not found"));
        shiftAssignmentRepository.delete(shiftAssignment);
    }

    public ShiftAssignmentResponse convertToResponse(ShiftAssignment shiftAssignment) {
        ShiftAssignmentResponse response = new ShiftAssignmentResponse();
        response.setId(shiftAssignment.getId());
        response.setEmployeeId(shiftAssignment.getEmployee().getId());
        response.setWorkShiftId(shiftAssignment.getWorkShift().getId());
        response.setWorkDate(shiftAssignment.getWorkDate());
        response.setNote(shiftAssignment.getNote());
        return response;
    }
}