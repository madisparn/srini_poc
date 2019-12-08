package org.bashhead.srini.country.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@JsonDeserialize(builder = Country.CountryBuilder.class)
@Builder
public class Country {

  private Long id;
  private String name;

  @JsonPOJOBuilder(withPrefix = "")
  public static class CountryBuilder {
  }
}
