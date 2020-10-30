package com.game.lobby;

import com.game.player.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@EqualsAndHashCode(of = "playerOne")
public class Lobby {

    private int number = ThreadLocalRandom
            .current()
            .nextInt(3, 99 + 1);

    private Player playerOne;
    private Player playerTwo;

    private Turn turn = Turn.PLAYER_ONE;

    public Lobby(Player player) {
        this.playerOne = player;
    }

    public boolean isReady() {
        return playerTwo != null;
    }

    public boolean isGameOver() {
        return number == 1;
    }

    public void setNextPlayerTurn(String playerName) {
        Turn turn;

        if (Objects.equals(playerName, playerOne.getName())) {
            turn = Turn.PLAYER_TWO;
        } else if (Objects.equals(playerName, playerTwo.getName())) {
            turn = Turn.PLAYER_ONE;
        } else {
            turn = Turn.PLAYER_ONE;
        }

        this.turn = turn;
    }

    public enum Turn {
        PLAYER_ONE, PLAYER_TWO
    }
}
