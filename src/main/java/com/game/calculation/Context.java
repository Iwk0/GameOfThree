package com.game.calculation;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Context {

    private final Strategy strategy;

    public int executeStrategy(int number) {
        return strategy.doOperation(number);
    }
}