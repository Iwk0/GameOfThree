package com.game.calculation;

class OperationDivideAddOne implements Strategy {

    @Override
    public int doOperation(int number) throws ArithmeticException {
        if ((number + 1) % 3 != 0) {
            throw new ArithmeticException();
        }

        return (number + 1) / 3;
    }
}
