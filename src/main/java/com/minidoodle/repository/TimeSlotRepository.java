package com.minidoodle.repository;

import com.minidoodle.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByCalendarId(Long calendarId);
    
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.calendar.id = :calendarId AND ts.startTime >= :startTime AND ts.endTime <= :endTime")
    List<TimeSlot> findAvailableSlotsInTimeRange(Long calendarId, Instant startTime, Instant endTime);
    
    List<TimeSlot> findByCalendarIdAndStatus(Long calendarId, TimeSlot.TimeSlotStatus status);
    
    default List<TimeSlot> findAvailableTimeSlots(Long calendarId) {
        return findByCalendarIdAndStatus(calendarId, TimeSlot.TimeSlotStatus.AVAILABLE);
    }
} 