package com.minidoodle.repository;

import com.minidoodle.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByParticipantsId(Long userId);
} 