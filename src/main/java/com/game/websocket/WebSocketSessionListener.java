package com.game.websocket;

import com.game.lobby.LobbyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Log4j2
@Component
@RequiredArgsConstructor
public class WebSocketSessionListener {

    private final LobbyService lobbyService;

    @EventListener
    public void connectionEstablished(SessionConnectedEvent sce) {
        MessageHeaders msgHeaders = sce
                .getMessage()
                .getHeaders();

        Principal principal = (Principal) msgHeaders.get("simpUser");

        if (principal == null) {
            return;
        }

        String playerName = principal.getName();
        log.info("Added player: " + playerName);

        lobbyService.createLobby(playerName);
    }

    @EventListener
    public void disconnected(SessionDisconnectEvent sde) {
        MessageHeaders msgHeaders = sde
                .getMessage()
                .getHeaders();

        Principal principal = (Principal) msgHeaders.get("simpUser");

        if (principal == null) {
            return;
        }

        String playerName = principal.getName();
        log.info("Removed player: " + playerName);

        lobbyService.removeLobby(playerName);
    }
}