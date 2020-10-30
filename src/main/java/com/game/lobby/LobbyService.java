package com.game.lobby;

import com.game.lobby.dto.GameSetup;
import com.game.player.Player;
import com.game.player.Type;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class LobbyService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Set<Lobby> lobbies = new HashSet<>();

    public void createLobby(String playerName) {
        findEmptyLobby()
                .ifPresentOrElse(
                        lobby -> {
                            log.info("Second player join the server.");
                            lobby.setPlayerTwo(new Player(playerName));
                        },
                        () -> {
                            log.info("New lobby was created.");
                            Player player = new Player(playerName);
                            lobbies.add(new Lobby(player));
                        }
                );
    }

    public void removeLobby(String playerName) {
        Optional<Lobby> optionalLobby = findByPlayerName(playerName);

        if (optionalLobby.isEmpty()) {
            log.info("The lobby is already deleted or never exists.");
            return;
        }

        Lobby lobby = optionalLobby.get();
        sendNotificationOnConnectionChange(lobby, GameSetup.Status.DISCONNECTED);

        lobbies.remove(lobby);

        log.info("Lobby was successfully deleted.");
    }

    public void connectionEstablished(String playerName) {
        findByPlayerName(playerName)
                .filter(Lobby::isReady)
                .ifPresent(lobby -> sendNotificationOnConnectionChange(lobby, GameSetup.Status.CONNECTED));
    }

    private void sendNotificationOnConnectionChange(Lobby lobby, GameSetup.Status status) {
        GameSetup gameSetup = GameSetup
                .builder()
                .number(lobby.getNumber())
                .status(status)
                .build();

        simpMessagingTemplate.convertAndSendToUser(
                lobby.getPlayerOne().getName(),
                "/notification/game-setup",
                gameSetup
        );

        simpMessagingTemplate.convertAndSendToUser(
                lobby.getPlayerTwo().getName(),
                "/notification/game-setup",
                gameSetup
        );
    }

    private Optional<Lobby> findEmptyLobby() {
        return lobbies
                .stream()
                .filter(lobby -> !lobby.isReady())
                .findFirst();
    }

    public Optional<Lobby> findByPlayerName(String playerName) {
        return lobbies
                .stream()
                .filter(lobby -> {
                            Player playerTwo = lobby.getPlayerTwo();
                            Player playerOne = lobby.getPlayerOne();

                            boolean isFirstPlayer = Objects.equals(playerOne.getName(), playerName);
                            boolean isSecondPlayer = playerTwo != null && Objects.equals(playerTwo.getName(), playerName);

                            return isFirstPlayer || isSecondPlayer;
                        }
                )
                .findFirst();
    }
}