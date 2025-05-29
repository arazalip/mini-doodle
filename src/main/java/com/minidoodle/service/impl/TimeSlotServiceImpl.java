package com.minidoodle.service.impl;

import com.minidoodle.dto.TimeSlotDTO;
import com.minidoodle.exception.TimeSlotException;
import com.minidoodle.mapper.TimeSlotMapper;
import com.minidoodle.model.TimeSlot;
import com.minidoodle.model.TimeSlotStatus;
import com.minidoodle.model.User;
import com.minidoodle.repository.TimeSlotRepository;
import com.minidoodle.repository.UserRepository;
import com.minidoodle.service.TimeSlotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.minidoodle.mapper.TimeSlotMapper.toInstant;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final TimeSlotMapper timeSlotMapper;

    @Override
    public TimeSlotDTO createTimeSlot(TimeSlotDTO timeSlotDTO) {

        User user = userRepository.findById(timeSlotDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + timeSlotDTO.getUserId()));
        TimeSlot timeSlot = timeSlotMapper.toEntity(timeSlotDTO, user);

        if(!timeSlot.isValid()) {
            throw new TimeSlotException("time slot start and endtime are not valid");
        }
        TimeSlot createdTimeSlot = timeSlotRepository.save(timeSlot);
        return timeSlotMapper.toDTO(createdTimeSlot);
    }

    @Override
    public TimeSlotDTO updateTimeSlot(Long id, TimeSlotDTO timeSlotDTO) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TimeSlot not found with id: " + id));
        timeSlotMapper.updateEntity(timeSlot, timeSlotDTO, null);

        if(!timeSlot.isValid()) {
            throw new TimeSlotException("time slot start and endtime are not valid");
        }
        TimeSlot updatedTimeSlot = timeSlotRepository.save(timeSlot);
        return timeSlotMapper.toDTO(updatedTimeSlot);
    }

    @Override
    public void deleteTimeSlot(Long id) {
        if (!timeSlotRepository.existsById(id)) {
            throw new EntityNotFoundException("TimeSlot not found with id: " + id);
        }
        timeSlotRepository.deleteById(id);
    }

    @Override
    public TimeSlotDTO getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id)
                .map(timeSlotMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("TimeSlot not found with id: " + id));

    }

    @Override
    public List<TimeSlotDTO> getTimeSlotsByUserId(Long userId) {
        return timeSlotRepository.findByCalendarId(userId).stream()
                .map(timeSlotMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSlotDTO> getAvailableTimeSlots(Long userId) {
        Long calendarId = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId)).getCalendar().getId();
        return timeSlotRepository.findAvailableTimeSlots(calendarId).stream()
                .map(timeSlotMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSlotDTO> getTimeSlotsInRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        Long calendarId = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId)).getCalendar().getId();
        return timeSlotRepository.findAvailableSlotsInTimeRange(calendarId, toInstant(startTime), toInstant(endTime)).stream()
                .map(timeSlotMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TimeSlotDTO markTimeSlotAsBusy(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TimeSlot not found with id: " + id));
        timeSlot.setStatus(TimeSlotStatus.BUSY);
        TimeSlot updatedTimeSlot = timeSlotRepository.save(timeSlot);
        return timeSlotMapper.toDTO(updatedTimeSlot);
    }

    @Override
    public TimeSlotDTO markTimeSlotAsAvailable(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TimeSlot not found with id: " + id));
        timeSlot.setStatus(TimeSlotStatus.AVAILABLE);
        TimeSlot updatedTimeSlot = timeSlotRepository.save(timeSlot);
        return timeSlotMapper.toDTO(updatedTimeSlot);
    }

    @Override
    public TimeSlotDTO markTimeSlotAsBooked(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TimeSlot not found with id: " + id));
        timeSlot.setStatus(TimeSlotStatus.BOOKED);
        TimeSlot updatedTimeSlot = timeSlotRepository.save(timeSlot);
        return timeSlotMapper.toDTO(updatedTimeSlot);
    }
} 