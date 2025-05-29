package com.minidoodle.service;

import com.minidoodle.dto.TimeSlotDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotService {
    TimeSlotDTO createTimeSlot(TimeSlotDTO timeSlotDTO);
    TimeSlotDTO updateTimeSlot(Long id, TimeSlotDTO timeSlotDTO);
    void deleteTimeSlot(Long id);
    TimeSlotDTO getTimeSlotById(Long id);
    List<TimeSlotDTO> getTimeSlotsByUserId(Long userId);
    List<TimeSlotDTO> getAvailableTimeSlots(Long userId);
    List<TimeSlotDTO> getTimeSlotsInRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    TimeSlotDTO markTimeSlotAsBusy(Long id);
    TimeSlotDTO markTimeSlotAsAvailable(Long id);
    TimeSlotDTO markTimeSlotAsBooked(Long id);
} 