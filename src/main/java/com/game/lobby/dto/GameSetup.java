package com.game.lobby.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameSetup {

    private final Status status;
    private final int number;

    public enum Status {
        CONNECTED, DISCONNECTED
    }
}
