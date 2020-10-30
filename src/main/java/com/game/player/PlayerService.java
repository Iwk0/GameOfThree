package com.game.player;

import com.game.calculation.CalculationFactory;
import com.game.lobby.Lobby;
import com.game.lobby.LobbyService;
import com.game.lobby.exception.LobbyNotFoundException;
import com.game.player.dto.*;
import com.game.player.exception.IllegalTurnException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LobbyService lobbyService;

    public void calculate() {
        Set<Lobby> lobbies = lobbyService.findByType(Type.AUTOMATICALLY);

        for (Lobby lobby : lobbies) {
            Optional<Player> player = createPlayerByType(lobby);

            if (player.isEmpty()) {
                continue;
            }

            String playerName = player.get()
                    .getName();

            if (!playerRestrictionTurn(lobby, playerName)) {
                continue;
            }

            int number = calculate(lobby.getNumber());

            lobby.setNextPlayerTurn(playerName);
            lobby.setNumber(number);

            sendCalculateResponse(lobby);

            if (lobby.isGameOver()) {
                notifyLobbyForWinner(playerName, lobby);
                lobbyService.removeLobby(playerName);
            }
        }
    }

    private int calculate(int number) {
        if (number % 3 == 0) {
            return number / 3;
        } else if ((number - 1) % 3 == 0) {
            return (number - 1) / 3;
        } else {
            return (number + 1) / 3;
        }
    }

    private Optional<Player> createPlayerByType(Lobby lobby) {
        if (Lobby.Turn.PLAYER_ONE == lobby.getTurn()) {
            if (lobby.getPlayerOne().getType() != Type.AUTOMATICALLY) {
                return Optional.empty();
            }

            return Optional.of(lobby.getPlayerOne());
        } else {
            if (lobby.getPlayerTwo().getType() != Type.AUTOMATICALLY) {
                return Optional.empty();
            }

            return Optional.of(lobby.getPlayerTwo());
        }
    }

    public void calculate(PlayerChoiceRequest playerChoiceRequest, String playerName) {
        Lobby lobby = lobbyService.findByPlayerName(playerName)
                .filter(Lobby::isReady)
                .orElseThrow(LobbyNotFoundException::new);

        if (!playerRestrictionTurn(lobby, playerName)) {
            throw new IllegalTurnException();
        }

        PlayerChoiceRequest.Value value = playerChoiceRequest.getValue();
        int number = CalculationFactory
                .getContext(value)
                .executeStrategy(lobby.getNumber());

        lobby.setNextPlayerTurn(playerName);
        lobby.setNumber(number);

        sendCalculateResponse(lobby);

        if (lobby.isGameOver()) {
            notifyLobbyForWinner(playerName, lobby);
            lobbyService.removeLobby(playerName);
        }
    }

    private boolean playerRestrictionTurn(Lobby lobby, String playerName) {
        if (Objects.equals(lobby.getPlayerOne().getName(), playerName)) {
            if (Lobby.Turn.PLAYER_TWO == lobby.getTurn()) {
                return false;
            }

            notifyNextPlayer(lobby.getPlayerTwo().getName());
        } else if (Objects.equals(lobby.getPlayerTwo().getName(), playerName)) {
            if (Lobby.Turn.PLAYER_ONE == lobby.getTurn()) {
                return false;
            }

            notifyNextPlayer(lobby.getPlayerOne().getName());
        }

        return true;
    }

    private void notifyNextPlayer(String playerName) {
        PlayerNextTurnResponse build = PlayerNextTurnResponse
                .builder()
                .build();

        simpMessagingTemplate.convertAndSendToUser(
                playerName,
                "/notification/player-next-turn",
                build
        );
    }

    private void notifyLobbyForWinner(String playerName, Lobby lobby) {
        String playerOne = lobby.getPlayerOne().getName();
        String playerTwo = lobby.getPlayerTwo().getName();

        if (Objects.equals(playerName, playerOne)) {
            sendFinalStatus(playerOne, GameOverResponse.Status.WIN);
            sendFinalStatus(playerTwo, GameOverResponse.Status.LOSE);
        } else {
            sendFinalStatus(playerTwo, GameOverResponse.Status.WIN);
            sendFinalStatus(playerOne, GameOverResponse.Status.LOSE);
        }
    }

    private void sendFinalStatus(String player, GameOverResponse.Status status) {
        GameOverResponse gameOverResponse = GameOverResponse
                .builder()
                .status(status)
                .build();

        simpMessagingTemplate.convertAndSendToUser(
                player,
                "/notification/game-over",
                gameOverResponse
        );
    }

    private void sendCalculateResponse(Lobby lobby) {
        GameResponse gameResponse = GameResponse
                .builder()
                .number(lobby.getNumber())
                .build();

        String playerOne = lobby
                .getPlayerOne()
                .getName();

        if (playerOne == null) {
            return;
        }

        simpMessagingTemplate.convertAndSendToUser(
                playerOne,
                "/notification/game-result",
                gameResponse
        );

        String playerTwo = lobby
                .getPlayerTwo()
                .getName();

        if (playerTwo == null) {
            return;
        }

        simpMessagingTemplate.convertAndSendToUser(
                playerTwo,
                "/notification/game-result",
                gameResponse
        );
    }

    public void assignType(PlayerTypeRequest playerTypeRequest, String playerName) {
        Lobby lobby = lobbyService.findByPlayerName(playerName)
                .filter(Lobby::isReady)
                .orElseThrow(LobbyNotFoundException::new);

        Player player = Optional.of(lobby.getPlayerOne())
                .filter(p -> Objects.equals(p.getName(), playerName))
                .orElse(lobby.getPlayerTwo());

        player.setType(playerTypeRequest.getType());
    }
}
