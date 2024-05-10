package com.br.multicloudecore.gcpmodule.models.users;

import lombok.Data;

/**
 * The `UserDto` class represents a DTO used for transferring user information.
 * It contains the first name, last name, email, and password of a user.
 */
@Data
public class UserDto {

  private String primeiroNome;
  private String segundoNome;
  private String email;
  private String password;
}
