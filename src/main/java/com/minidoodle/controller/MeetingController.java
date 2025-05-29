package com.minidoodle.controller;

import com.minidoodle.dto.MeetingDTO;
import com.minidoodle.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingService meetingService;

    @PostMapping
    public ResponseEntity<MeetingDTO> createMeeting(@Valid @RequestBody MeetingDTO meetingDTO) {
        MeetingDTO createdMeeting = meetingService.createMeeting(meetingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMeeting);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingDTO> getMeetingById(@PathVariable Long id) {
        MeetingDTO meeting = meetingService.getMeetingById(id);
        return ResponseEntity.ok(meeting);
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<MeetingDTO>> getMeetingsByOrganizerId(@PathVariable Long organizerId) {
        List<MeetingDTO> meetings = meetingService.getMeetingsByOrganizerId(organizerId);
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<MeetingDTO>> getMeetingsByParticipantId(@PathVariable Long participantId) {
        List<MeetingDTO> meetings = meetingService.getMeetingsByParticipantId(participantId);
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/timeslot/{timeSlotId}")
    public ResponseEntity<Set<MeetingDTO>> getMeetingsByTimeSlotId(@PathVariable Long timeSlotId) {
        Set<MeetingDTO> meetings = meetingService.getMeetingsByTimeSlotId(timeSlotId);
        return ResponseEntity.ok(meetings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingDTO> updateMeeting(@PathVariable Long id, @Valid @RequestBody MeetingDTO meetingDTO) {
        MeetingDTO updatedMeeting = meetingService.updateMeeting(id, meetingDTO);
        return ResponseEntity.ok(updatedMeeting);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{meetingId}/participants/{userId}")
    public ResponseEntity<MeetingDTO> addParticipant(@PathVariable Long meetingId, @PathVariable Long userId) {
        MeetingDTO updatedMeeting = meetingService.addParticipant(meetingId, userId);
        return ResponseEntity.ok(updatedMeeting);
    }

    @DeleteMapping("/{meetingId}/participants/{userId}")
    public ResponseEntity<MeetingDTO> removeParticipant(@PathVariable Long meetingId, @PathVariable Long userId) {
        MeetingDTO updatedMeeting = meetingService.removeParticipant(meetingId, userId);
        return ResponseEntity.ok(updatedMeeting);
    }
} 