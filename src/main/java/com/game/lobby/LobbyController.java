package com.game.lobby;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class LobbyController {

    private final com.game.lobby.LobbyService lobbyService;

    @SubscribeMapping(value = "/notification/game-setup")
    public void connectionEstablished(Principal principal) {
        String playerName = principal.getName();
        lobbyService.connectionEstablished(playerName);
    }
}
