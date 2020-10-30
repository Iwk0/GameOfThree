package com.game.player.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameOverResponse {

    private final Status status;

    public enum Status {
        WIN, LOSE
    }
}