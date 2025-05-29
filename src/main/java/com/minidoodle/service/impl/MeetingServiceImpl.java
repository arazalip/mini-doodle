package com.minidoodle.service.impl;

import com.minidoodle.dto.MeetingDTO;
import com.minidoodle.mapper.MeetingMapper;
import com.minidoodle.model.Meeting;
import com.minidoodle.model.User;
import com.minidoodle.repository.MeetingRepository;
import com.minidoodle.repository.UserRepository;
import com.minidoodle.service.MeetingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingMapper meetingMapper;

    @Override
    public MeetingDTO createMeeting(MeetingDTO meetingDTO) {
        Meeting meeting = meetingMapper.toEntity(meetingDTO);
        Meeting createdMeeting = meetingRepository.save(meeting);
        return meetingMapper.toDTO(createdMeeting);
    }

    @Override
    public MeetingDTO updateMeeting(Long id, MeetingDTO meetingDTO) {
        if (!meetingRepository.existsById(id)) {
            throw new EntityNotFoundException("Meeting not found with id: " + id);
        }
        Meeting meeting = meetingMapper.toEntity(meetingDTO);
        meeting.setId(id);
        Meeting updatedMeeting = meetingRepository.save(meeting);
        return meetingMapper.toDTO(updatedMeeting);
    }

    @Override
    public void deleteMeeting(Long id) {
        if (!meetingRepository.existsById(id)) {
            throw new EntityNotFoundException("Meeting not found with id: " + id);
        }
        meetingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
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
        Meeting updatedMeeting = meetingRepository.save(meeting);
        return meetingMapper.toDTO(updatedMeeting);
    }

    @Override
    public Set<MeetingDTO> getMeetingsByTimeSlotId(Long timeSlotId) {
        return meetingRepository.findByTimeSlotId(timeSlotId).stream()
                .map(meetingMapper::toDTO)
                .collect(Collectors.toSet());
    }
} 