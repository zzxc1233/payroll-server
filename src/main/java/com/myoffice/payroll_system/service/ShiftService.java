package com.myoffice.payroll_system.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myoffice.payroll_system.entity.ShiftAssignment;
import com.myoffice.payroll_system.repository.ShiftAssignmentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    
    public List<ShiftAssignment> getMonthlyShift(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return shiftAssignmentRepository.findByWorkDateBetween(start, end);
    }

    @Transactional
    public ShiftAssignment saveShift(ShiftAssignment shiftAssignment) {
        return shiftAssignmentRepository.save(shiftAssignment);
    }

    public List<ShiftAssignment> getShiftsByEmployee(Long employeeId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return shiftAssignmentRepository.findByEmployeeIdAndWorkDateBetween(employeeId, start, end);
    }
}
