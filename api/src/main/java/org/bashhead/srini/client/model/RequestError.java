package org.bashhead.srini.client.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RequestError {

  private String type;
  private String message;
}
