package com.game.error;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorHandlerService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void errorHandler(Error error, String username) {
        simpMessagingTemplate.convertAndSendToUser(
                username,
                "/notification/error",
                error
        );
    }
}
