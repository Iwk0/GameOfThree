package com.game.player;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlayerScheduler {

    private final PlayerService playerService;

    @Scheduled(cron = "*/1 * * * * *")
    public void playAutomatically() {
        playerService.calculate();
    }
}
