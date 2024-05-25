package com.br.multicloudecore.gcpmodule.controllers;

import com.br.multicloudecore.gcpmodule.models.users.UserDto;
import com.br.multicloudecore.gcpmodule.service.ai.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * This class represents the controller for user profiles. It handles the HTTP requests related
 * to creating and managing user profiles.
 */
@RestController
@RequiredArgsConstructor
public class ProfileController {

  private final UserService userService;

  /**
   * Cria um novo usuário com base nos dados fornecidos.
   *
   * @param userDto O objeto UserDto contendo os dados do usuário a serem salvos.
   * @return Um objeto ResponseEntity contendo uma mensagem de sucesso e os dados do usuário salvos,
   *         ou uma mensagem de erro em caso de falha.
   */
  @PostMapping("/create")
  public ResponseEntity<Object> save(@RequestBody UserDto userDto) {
    try {
      userService.saveUser(userDto);
      return generateResponse("Items saved successfully!", HttpStatus.OK, userDto);
    } catch (Exception e) {
      return generateErrorResponse("Failed to save items: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Generates a response entity with the given message, HTTP status, and response object.
   *
   * @param message The message to be included in the response.
   * @param status  The HTTP status of the response.
   * @param responseObj The object to be included in the response.
   * @return A ResponseEntity object with the generated response.
   */
  public static ResponseEntity<Object> generateResponse(String message,
                                                        HttpStatus status,
                                                        Object responseObj) {
    Map<String, Object> map = new HashMap<>();
    map.put("message", message);
    map.put("status", status.value());
    map.put("data", responseObj);

    return new ResponseEntity<>(map, status);
  }

  private ResponseEntity<Object> generateErrorResponse(String message, HttpStatus status) {
    Map<String, String> error = new HashMap<>();
    error.put("error", message);
    return new ResponseEntity<>(error, status);
  }
}
