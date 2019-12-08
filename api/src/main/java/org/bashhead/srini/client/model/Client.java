package org.bashhead.srini.client.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@JsonDeserialize(builder = Client.ClientBuilder.class)
@Builder(toBuilder = true)
public class Client {

  private Long id;
  @NotBlank
  @Size(max = 64)
  private String firstName;
  @NotBlank
  @Size(max = 64)
  private String lastName;
  @NotBlank
  @Size(max = 32)
  private String username;
  @Email
  @Size(max = 64)
  private String email;
  @Size(max = 256)
  private String address;
  @NotNull
  private Long countryId;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ClientBuilder {

  }

}
