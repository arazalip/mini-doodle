package com.minidoodle.repository;

import com.minidoodle.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByOrganizerId(Long organizerId);
    
    @Query("SELECT m FROM Meeting m JOIN m.participants p WHERE p.id = :participantId")
    List<Meeting> findByParticipantId(Long participantId);
    
    Set<Meeting> findByTimeSlotId(Long timeSlotId);
} 