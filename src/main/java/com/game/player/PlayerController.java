package com.game.player;

import com.game.error.Error;
import com.game.error.ErrorHandlerService;
import com.game.lobby.exception.LobbyNotFoundException;
import com.game.player.dto.PlayerChoiceRequest;
import com.game.player.dto.PlayerTypeRequest;
import com.game.player.exception.IllegalTurnException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final ErrorHandlerService errorHandler;

    @MessageMapping(value = "/play")
    public void play(@Payload PlayerChoiceRequest playerChoiceRequest, Principal principal) {
        playerService.calculate(playerChoiceRequest, principal.getName());
    }

    @MessageMapping(value = "/type")
    public void changeType(@Payload PlayerTypeRequest playerTypeRequest, Principal principal) {
        playerService.assignType(playerTypeRequest, principal.getName());
    }

    @MessageExceptionHandler
    public void handleException(ArithmeticException ex, Principal principal) {
        errorHandler.errorHandler(new Error("CANNOT_DIVIDE"), principal.getName());
    }

    @MessageExceptionHandler
    public void handleException(IllegalTurnException ex, Principal principal) {
        errorHandler.errorHandler(new Error("ILLEGAL_TURN"), principal.getName());
    }

    @MessageExceptionHandler
    public void handleException(LobbyNotFoundException ex, Principal principal) {
        errorHandler.errorHandler(new Error("LOBBY_NOT_FOUND"), principal.getName());
    }
}
