package com.game.websocket;

import lombok.AllArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
class StompPrincipal implements Principal {

    private final String name;

    @Override
    public String getName() {
        return name;
    }
}
