package com.game.player.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerNextTurnResponse {

    private final String status = "YOUR_TURN";
}