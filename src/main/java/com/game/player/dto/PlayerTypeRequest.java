package com.game.player.dto;

import com.game.player.Type;
import lombok.Getter;

@Getter
public class PlayerTypeRequest {

    private final Type type = Type.MANUAL;
}
