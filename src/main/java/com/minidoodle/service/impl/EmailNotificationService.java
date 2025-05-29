package com.minidoodle.service.impl;

import com.minidoodle.model.Meeting;
import com.minidoodle.model.User;
import com.minidoodle.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
public class EmailNotificationService implements NotificationService {

    @Override
    @Async
    public CompletableFuture<Void> sendMeetingInvitation(User user, Meeting meeting) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("Sending meeting invitation email to participant: {} in meeting: {}",
                    user.getName(),
                    meeting.getTitle());

                Thread.sleep(100);

                log.info("Email content: You have been invited to '{}' by {}. ",
                    meeting.getTitle(), meeting.getOrganizer().getName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CompletionException("Failed to send meeting invitation email", e);
            } catch (Exception e) {
                log.error("Error sending meeting invitation email", e);
                throw new CompletionException("Failed to send meeting invitation email", e);
            }
        });
    }

    @Override
    @Async
    public CompletableFuture<Void> sendMeetingAcceptance(User user, Meeting meeting) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("Sending meeting acceptance notification to {} for meeting: {}",
                    meeting.getOrganizer().getEmail(), meeting.getTitle());

                Thread.sleep(100);

                log.info("Email content: {} has accepted the invitation to '{}'",
                    user.getName(), meeting.getTitle());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CompletionException("Failed to send meeting acceptance email", e);
            } catch (Exception e) {
                log.error("Error sending meeting acceptance email", e);
                throw new CompletionException("Failed to send meeting acceptance email", e);
            }
        });
    }
} 