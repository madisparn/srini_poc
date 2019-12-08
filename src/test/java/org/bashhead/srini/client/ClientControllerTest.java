package org.bashhead.srini.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.github.javafaker.Faker;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bashhead.srini.client.model.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper json = new ObjectMapper();

  @Test
  public void requiresAuthentication() throws Exception {
    mockMvc.perform(get("/client"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  public void shouldReturnListOfClients() throws Exception {
    TestingAuthenticationToken user = userToken("user2");
    Client client1 = randomClient(user);
    Client client2 = randomClient(user);

    List<Client> list = getClients(user);
    assertThat(list)
        .containsOnlyOnce(client1, client2);
  }

  @Test
  public void shouldNotReturnListOfOtherClients() throws Exception {
    randomClient(userToken("user2"));

    List<Client> list = getClients(userToken("any_user3"));
    assertThat(list).isEmpty();
  }

  @Test
  public void canNotCreateWithInvalidCountry() throws Exception {
    createClientRequest(randomClient().toBuilder()
        .countryId(5446L)
        .build(), userToken("user2"))
    .andExpect(status().isInternalServerError());
  }

  @Test
  public void canNotCreateWithInvalidDuplicateUsername() throws Exception {
    Client client1 = randomClient(userToken("user2"));

    createClientRequest(randomClient().toBuilder()
        .username(client1.getUsername())
        .build(), userToken("user3"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void canFetchClientById() throws Exception {
    randomClient(userToken("user2"));
    Client client2 = randomClient(userToken("user2"));

    Client dbClient = getClient(client2.getId(), userToken("user2"));
    assertThat(dbClient).isEqualTo(client2);
  }

  @Test
  public void canNotFetchOtherClientById() throws Exception {
    Client client = randomClient(userToken("user2"));

    mockMvc.perform(get("/client/" + client.getId())
        .with(user("other_user3")))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  public void canUpdateClient() throws Exception {
    Client client1 = randomClient(userToken("user2"));

    Client client2 = client1.toBuilder()
        .email(null)
        .lastName(new Faker().name().lastName())
        .countryId(2L)
        .build();

    mockMvc.perform(put("/client")
        .with(user("user2"))
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.writeValueAsBytes(client2))
    )
        .andExpect(status().isOk());

    Client dbClient = getClient(client2.getId(), userToken("user2"));
    assertThat(dbClient).isEqualTo(client2);
  }

  @Test
  public void canNotUpdateOtherClient() throws Exception {
    Client client1 = randomClient(userToken("user2"));
    Client client2 = client1.toBuilder()
        .lastName(new Faker().name().lastName())
        .build();

    mockMvc.perform(put("/client")
        .with(user("other_user3"))
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.writeValueAsBytes(client2))
    )
        .andExpect(status().isNotFound());
  }

  @Test
  public void canNotUpdateClientWithInvalidValue() throws Exception {
    Client client1 = randomClient(userToken("user2"));
    Client client2 = client1.toBuilder()
        .lastName(new Faker().lorem().fixedString(65))
        .build();

    byte[] result = mockMvc.perform(put("/client")
        .with(user("user2"))
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.writeValueAsBytes(client2))
    )
        .andExpect(status().isBadRequest())
        .andReturn()
        .getResponse().getContentAsByteArray();

    Map<String, String> errors = json.readValue(result, Map.class);
    assertThat(errors)
        .containsEntry("lastName", "size must be between 0 and 64");
  }

  private Client randomClient() {
    Faker faker = new Faker();
    return Client.builder()
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .username(faker.name().username())
        .address(faker.address().fullAddress())
        .email(faker.internet().emailAddress())
        .countryId(1L)
        .build();
  }

  private Client randomClient(Principal user) throws Exception {
    return createClient(randomClient(), user);
  }

  private Client createClient(Client client, Principal user) throws Exception {
    byte[] result = createClientRequest(client, user)
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsByteArray();
    return json.readValue(result, Client.class);
  }

  private ResultActions createClientRequest(Client client, Principal user) throws Exception {
    return mockMvc.perform(post("/client")
        .content(json.writeValueAsBytes(client))
        .contentType(MediaType.APPLICATION_JSON)
        .with(user(user.getName()))
        .with(csrf())
    );
  }

  private TestingAuthenticationToken userToken(String username) {
    return new TestingAuthenticationToken(username, "");
  }

  private List<Client> getClients(Principal user) throws Exception {
    byte[] result = mockMvc.perform(get("/client")
        .with(user(user.getName())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsByteArray();

    CollectionLikeType type = json.getTypeFactory()
        .constructCollectionLikeType(ArrayList.class, Client.class);
    return json.readValue(result, type);
  }

  private Client getClient(long id, Principal user) throws Exception {
    byte[] result = mockMvc.perform(get("/client/" + id)
        .with(user(user.getName())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsByteArray();
    return json.readValue(result, Client.class);
  }

}