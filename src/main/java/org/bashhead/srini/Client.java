package org.bashhead.srini;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Value;

@Value
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

  @XmlTransient
  private String assignee;

}
