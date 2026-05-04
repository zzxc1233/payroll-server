package com.myoffice.payroll_system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myoffice.payroll_system.entity.WorkShift;
import com.myoffice.payroll_system.repository.WorkShiftRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkShiftService {
    private final WorkShiftRepository workShiftRepository;

    public List<WorkShift> getAllWorkShifts() {
        return workShiftRepository.findAll();
    }

    @Transactional
    public WorkShift createWorkShift(WorkShift workShift) {
        return workShiftRepository.save(workShift);
    }

    @Transactional
    public WorkShift updateWorkShift(Long id, WorkShift workShift) {
        WorkShift existingWorkShift = workShiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Work shift not found"));
        existingWorkShift.setShiftName(workShift.getShiftName());
        existingWorkShift.setStartTime(workShift.getStartTime());
        existingWorkShift.setEndTime(workShift.getEndTime());
        existingWorkShift.setExtraHourRate(workShift.getExtraHourRate());
        return workShiftRepository.save(existingWorkShift);
    }

    @Transactional
    public void deleteWorkShift(Long id) {
        workShiftRepository.deleteById(id);
    }
}
