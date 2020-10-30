package com.game.calculation;

import com.game.player.dto.PlayerChoiceRequest;

public class CalculationFactory {

    public static Context getContext(PlayerChoiceRequest.Value value) {
        if (value == PlayerChoiceRequest.Value.MINUS_ONE) {
            return new Context(new OperationDivideSubtractOne());
        } else if (value == PlayerChoiceRequest.Value.ZERO) {
            return new Context(new OperationDivide());
        } else {
            return new Context(new OperationDivideAddOne());
        }
    }
}