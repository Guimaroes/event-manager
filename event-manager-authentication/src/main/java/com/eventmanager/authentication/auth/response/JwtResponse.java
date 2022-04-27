package com.eventmanager.authentication.auth.response;

import com.eventmanager.authentication.user.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
  private String token;
  private final String type = "Bearer";
  @JsonProperty("user")
  private UserDTO userDTO;
}
