package com.minidoodle.service;

import com.minidoodle.model.Meeting;
import com.minidoodle.model.User;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    CompletableFuture<Void> sendMeetingInvitation(User user, Meeting meeting);
    CompletableFuture<Void> sendMeetingAcceptance(User user, Meeting meeting);
} 