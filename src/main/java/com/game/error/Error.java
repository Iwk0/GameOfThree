package com.game.error;

import lombok.Getter;

@Getter
public class Error {

    private final String error;

    public Error(String error) {
        this.error = error;
    }
}
