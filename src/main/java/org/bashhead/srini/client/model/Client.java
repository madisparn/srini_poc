package org.bashhead.srini.client.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@JsonDeserialize(builder = Client.ClientBuilder.class)
@Builder(toBuilder = true)
public class Client {

  private Long id;
  @NotBlank
  @Max(64)
  private String firstName;
  @NotBlank
  @Max(64)
  private String lastName;
  @NotBlank
  @Max(32)
  private String username;
  @Email
  @Max(64)
  private String email;
  @Max(256)
  private String address;
  @NotNull
  private Long countryId;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ClientBuilder {
  }

}
