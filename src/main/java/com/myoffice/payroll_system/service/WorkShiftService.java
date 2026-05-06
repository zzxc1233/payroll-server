package com.myoffice.payroll_system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myoffice.payroll_system.dto.WorkshiftDTO;
import com.myoffice.payroll_system.dto.WorkshiftDTO.WorkshiftRequest;
import com.myoffice.payroll_system.dto.WorkshiftDTO.WorkshiftResponse;
import com.myoffice.payroll_system.entity.WorkShift;
import com.myoffice.payroll_system.exception.ResourceNotFoundException;
import com.myoffice.payroll_system.repository.WorkShiftRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkShiftService {
    private final WorkShiftRepository workShiftRepository;

    public List<WorkshiftResponse> getAllWorkShifts() {
        List<WorkShift> workShifts = workShiftRepository.findAll();
        return workShifts.stream().map(this::convertToResponse).toList();
    }

    public WorkshiftResponse getWorkShiftById(Long id) {
        WorkShift workShift = workShiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work shift not found"));
        return convertToResponse(workShift);
    }

    @Transactional
    public WorkshiftResponse createWorkShift(WorkshiftRequest request) {
        WorkShift workShift = new WorkShift();
        workShift.setShiftName(request.getShiftName());
        workShift.setStartTime(request.getStartTime());
        workShift.setEndTime(request.getEndTime());
        workShift.setExtraHourRate(request.getExtraHourRate());
        WorkShift savedWorkShift = workShiftRepository.save(workShift);
        return convertToResponse(savedWorkShift);
    }

    @Transactional
    public WorkshiftResponse updateWorkShift(Long id, WorkshiftRequest request) {
        WorkShift existingWorkShift = workShiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work shift not found"));
        existingWorkShift.setShiftName(request.getShiftName());
        existingWorkShift.setStartTime(request.getStartTime());
        existingWorkShift.setEndTime(request.getEndTime());
        existingWorkShift.setExtraHourRate(request.getExtraHourRate());
        WorkShift updatedWorkShift = workShiftRepository.save(existingWorkShift);
        return convertToResponse(updatedWorkShift);
    }

    @Transactional
    public void deleteWorkShift(Long id) {
        workShiftRepository.deleteById(id);
    }

    private WorkshiftResponse convertToResponse(WorkShift workShift) {
        WorkshiftResponse response = new WorkshiftResponse();
        response.setId(workShift.getId());
        response.setShiftName(workShift.getShiftName());
        response.setStartTime(workShift.getStartTime());
        response.setEndTime(workShift.getEndTime());
        response.setExtraHourRate(workShift.getExtraHourRate());
        return response;
    }
}