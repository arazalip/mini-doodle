package com.minidoodle.service;

import com.minidoodle.dto.MeetingDTO;

import java.util.List;

public interface MeetingService {
    MeetingDTO createMeeting(MeetingDTO meetingDTO);
    MeetingDTO updateMeeting(Long id, MeetingDTO meetingDTO);
    void deleteMeeting(Long id);
    MeetingDTO getMeetingById(Long id);
    List<MeetingDTO> getMeetingsByOrganizerId(Long organizerId);
    List<MeetingDTO> getMeetingsByParticipantId(Long participantId);
    MeetingDTO addParticipant(Long meetingId, Long userId);
    MeetingDTO removeParticipant(Long meetingId, Long userId);
    MeetingDTO acceptInvitation(Long meetingId, Long participantId);
} 