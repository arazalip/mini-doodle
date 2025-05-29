package com.minidoodle.service.impl;

import com.minidoodle.dto.MeetingDTO;
import com.minidoodle.mapper.MeetingMapper;
import com.minidoodle.model.Meeting;
import com.minidoodle.model.TimeSlot;
import com.minidoodle.model.TimeSlotStatus;
import com.minidoodle.model.User;
import com.minidoodle.repository.MeetingRepository;
import com.minidoodle.repository.TimeSlotRepository;
import com.minidoodle.repository.UserRepository;
import com.minidoodle.service.MeetingService;
import com.minidoodle.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final MeetingMapper meetingMapper;
    private final NotificationService notificationService;

    @Override
    public MeetingDTO createMeeting(MeetingDTO meetingDTO) {
        TimeSlot timeSlot = timeSlotRepository.findById(meetingDTO.getTimeSlotId())
                .orElseThrow(() -> new EntityNotFoundException("Time slot not found with id: " + meetingDTO.getTimeSlotId()));

        Set<Long> allParticipantIds = new HashSet<>(meetingDTO.getParticipantIds());
        allParticipantIds.add(meetingDTO.getOrganizerId());

        User organizer = userRepository.findById(meetingDTO.getOrganizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found with id: " + meetingDTO.getOrganizerId()));

        List<User> participants = userRepository.findAllById(allParticipantIds);

        Meeting meeting = meetingMapper.toEntity(meetingDTO, organizer, timeSlot, new HashSet<>(participants));
        Meeting createdMeeting = meetingRepository.save(meeting);
        for (Long participantId : allParticipantIds) {
            bookTimeSlot(participantId, createdMeeting);
        }
        sendNotificationsAsync(meeting);
        return meetingMapper.toDTO(createdMeeting);
    }

    private void bookTimeSlot(Long participantId, Meeting meeting) {
        List<TimeSlot> availableSlots = timeSlotRepository.findAvailableTimeSlotsByUserIdAndTimeRange(
                participantId,
                meeting.getTimeSlot().getStartTime(),
                meeting.getTimeSlot().getEndTime(),
                TimeSlotStatus.AVAILABLE);

        TimeSlot matchingSlot = availableSlots.getFirst();
        matchingSlot.setMeeting(meeting);
        matchingSlot.setStatus(TimeSlotStatus.BOOKED);
        timeSlotRepository.save(matchingSlot);
    }

    @Override
    public MeetingDTO updateMeeting(Long id, MeetingDTO meetingDTO) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() ->  new EntityNotFoundException("Meeting not found with id: " + id));
        TimeSlot timeSlot = !meeting.getTimeSlot().getId().equals(meetingDTO.getTimeSlotId()) ?
                timeSlotRepository.findById(meetingDTO.getTimeSlotId())
                .orElseThrow(() ->  new EntityNotFoundException("TimeSlot not found with id: " + id)) : null;

        meetingMapper.updateEntity(meeting, meetingDTO, timeSlot);
        Meeting updatedMeeting = meetingRepository.save(meeting);

        sendNotificationsAsync(updatedMeeting);

        return meetingMapper.toDTO(updatedMeeting);
    }

    private void sendNotificationsAsync(Meeting meeting) {
        CompletableFuture.runAsync(() ->
            meeting.getParticipants().forEach(participant -> {
            try {
                notificationService.sendMeetingInvitation(participant, meeting).join();
            } catch (Exception e) {
                log.error("Failed to send meeting invitation notification", e);
            }
        }));
    }


    @Override
    public void deleteMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + id));

        meeting.getParticipants().forEach(p -> freeMeetingTimeSlot(meeting.getId(), p));

        meetingRepository.deleteById(id);
    }

    @Override
    public MeetingDTO getMeetingById(Long id) {
        return meetingRepository.findById(id)
                .map(meetingMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + id));
    }

    @Override
    public List<MeetingDTO> getMeetingsByOrganizerId(Long organizerId) {
        return meetingRepository.findByOrganizerId(organizerId).stream()
                .map(meetingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MeetingDTO> getMeetingsByParticipantId(Long participantId) {
        return meetingRepository.findByParticipantId(participantId).stream()
                .map(meetingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MeetingDTO addParticipant(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + meetingId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        meeting.getParticipants().add(user);
        bookTimeSlot(userId, meeting);
        Meeting updatedMeeting = meetingRepository.save(meeting);
        return meetingMapper.toDTO(updatedMeeting);
    }

    @Override
    public MeetingDTO removeParticipant(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + meetingId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        meeting.getParticipants().remove(user);

        freeMeetingTimeSlot(meetingId, user);

        Meeting updatedMeeting = meetingRepository.save(meeting);
        return meetingMapper.toDTO(updatedMeeting);
    }

    private static void freeMeetingTimeSlot(Long meetingId, User user) {
        user.getCalendar().getTimeSlots().stream()
                .filter(ts -> ts.getMeeting().getId().equals(meetingId)).findFirst()
                .ifPresent(slot -> slot.setStatus(TimeSlotStatus.AVAILABLE));
    }

    @Override
    @Transactional
    public MeetingDTO acceptInvitation(Long meetingId, Long participantId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + meetingId));
        User participant = userRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + participantId));

        if (!meeting.getParticipants().contains(participant)) {
            throw new IllegalStateException("User " + participantId + " is not a participant in meeting " + meetingId);
        }

        TimeSlot matchingSlot = participant.getCalendar().getTimeSlots().stream()
                .filter(ts -> ts.getMeeting().getId().equals(meetingId)).findFirst()
                .orElseThrow(() -> new IllegalStateException("TimeSlot not found for participant " + participantId + " with the meeting " + meetingId));

        matchingSlot.setStatus(TimeSlotStatus.BUSY);
        matchingSlot.setMeeting(meeting);
        timeSlotRepository.save(matchingSlot);

        CompletableFuture.runAsync(() -> {
            try {
                notificationService.sendMeetingAcceptance(participant, meeting).join();
            } catch (Exception e) {
                log.error("Failed to send meeting acceptance notification", e);
            }
        });

        return meetingMapper.toDTO(meeting);
    }
} 