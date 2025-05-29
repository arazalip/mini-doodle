package com.minidoodle.controller;

import com.minidoodle.dto.TimeSlotDTO;
import com.minidoodle.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {
    private final TimeSlotService timeSlotService;

    @PostMapping
    public ResponseEntity<TimeSlotDTO> createTimeSlot(@Valid @RequestBody TimeSlotDTO timeSlotDTO) {
        TimeSlotDTO createdTimeSlot = timeSlotService.createTimeSlot(timeSlotDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTimeSlot);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotDTO> getTimeSlotById(@PathVariable Long id) {
        TimeSlotDTO timeSlot = timeSlotService.getTimeSlotById(id);
        return ResponseEntity.ok(timeSlot);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TimeSlotDTO>> getTimeSlotsByUserId(@PathVariable Long userId) {
        List<TimeSlotDTO> timeSlots = timeSlotService.getTimeSlotsByUserId(userId);
        return ResponseEntity.ok(timeSlots);
    }

    @GetMapping("/user/{userId}/available")
    public ResponseEntity<List<TimeSlotDTO>> getAvailableTimeSlots(@PathVariable Long userId) {
        List<TimeSlotDTO> timeSlots = timeSlotService.getAvailableTimeSlots(userId);
        return ResponseEntity.ok(timeSlots);
    }

    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<TimeSlotDTO>> getTimeSlotsInRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<TimeSlotDTO> timeSlots = timeSlotService.getTimeSlotsInRange(userId, startTime, endTime);
        return ResponseEntity.ok(timeSlots);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeSlotDTO> updateTimeSlot(@PathVariable Long id, @Valid @RequestBody TimeSlotDTO timeSlotDTO) {
        TimeSlotDTO updatedTimeSlot = timeSlotService.updateTimeSlot(id, timeSlotDTO);
        return ResponseEntity.ok(updatedTimeSlot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/busy")
    public ResponseEntity<TimeSlotDTO> markTimeSlotAsBusy(@PathVariable Long id) {
        TimeSlotDTO updatedTimeSlot = timeSlotService.markTimeSlotAsBusy(id);
        return ResponseEntity.ok(updatedTimeSlot);
    }

    @PutMapping("/{id}/available")
    public ResponseEntity<TimeSlotDTO> markTimeSlotAsAvailable(@PathVariable Long id) {
        TimeSlotDTO updatedTimeSlot = timeSlotService.markTimeSlotAsAvailable(id);
        return ResponseEntity.ok(updatedTimeSlot);
    }

    @PutMapping("/{id}/booked")
    public ResponseEntity<TimeSlotDTO> markTimeSlotAsBooked(@PathVariable Long id) {
        TimeSlotDTO updatedTimeSlot = timeSlotService.markTimeSlotAsBooked(id);
        return ResponseEntity.ok(updatedTimeSlot);
    }
} 