package com.game.player.dto;

import lombok.Getter;

@Getter
public class PlayerChoiceRequest {

    private final Value value = Value.ONE;

    public enum Value {
        ONE, ZERO, MINUS_ONE
    }
}
