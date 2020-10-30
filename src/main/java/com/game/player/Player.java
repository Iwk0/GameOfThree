package com.game.player;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "name")
@RequiredArgsConstructor
public class Player {

    private final String name;
    private Type type = Type.MANUAL;
}
