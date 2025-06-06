package com.minidoodle.repository;

import com.minidoodle.model.TimeSlot;
import com.minidoodle.model.TimeSlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByCalendarId(Long calendarId);
    
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.calendar.id = :calendarId AND ts.startTime >= :startTime AND ts.endTime <= :endTime")
    List<TimeSlot> findAvailableSlotsInTimeRange(Long calendarId, Instant startTime, Instant endTime);
    
    List<TimeSlot> findByCalendarIdAndStatus(Long calendarId, TimeSlotStatus status);
    
    default List<TimeSlot> findAvailableTimeSlots(Long calendarId) {
        return findByCalendarIdAndStatus(calendarId, TimeSlotStatus.AVAILABLE);
    }

    @Query("SELECT ts FROM TimeSlot ts " +
           "JOIN FETCH ts.calendar c " +
           "JOIN FETCH c.user u " +
           "WHERE u.id = :userId " +
           "AND ts.status = :status " +
           "AND ((ts.startTime <= :endTime AND ts.endTime >= :startTime) " +
           "OR (ts.startTime >= :startTime AND ts.endTime <= :endTime)) " +
           "ORDER BY ts.startTime")
    List<TimeSlot> findAvailableTimeSlotsByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("status") TimeSlotStatus status);
} 