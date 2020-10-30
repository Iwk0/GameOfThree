package com.game.calculation;

class OperationDivide implements Strategy {

    @Override
    public int doOperation(int number) throws ArithmeticException {
        if (number % 3 != 0) {
            throw new ArithmeticException();
        }

        return number / 3;
    }
}
