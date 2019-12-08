package org.bashhead.srini.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import java.util.ArrayList;
import java.util.List;
import org.bashhead.srini.country.model.Country;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CountryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper json = new ObjectMapper();

  @Test
  public void requiresAuthentication() throws Exception {
    mockMvc.perform(get("/api/country"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  public void shouldReturnListOfCountries() throws Exception {
    byte[] result = mockMvc.perform(get("/api/country").with(user("random_user")))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsByteArray();

    CollectionLikeType type = json.getTypeFactory()
        .constructCollectionLikeType(ArrayList.class, Country.class);
    List<Country> list = json.readValue(result, type);
    assertThat(list).isNotEmpty();
    assertThat(list.get(0).getId())
        .isNotNull()
        .isPositive();
    assertThat(list)
        .extracting(Country::getName)
        .contains("Estonia", "Finland");
  }
}