package org.bashhead.srini.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.github.javafaker.Faker;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.bashhead.srini.client.model.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ClientDao dao;

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

  private Client randomClient(Principal user) {
    Faker faker = new Faker();
    return dao.create(Client.builder()
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .username(faker.name().username())
        .address(faker.address().fullAddress())
        .email(faker.internet().emailAddress())
        .countryId(1L)
        .build(), user);
  }

  private Client createClient(Client client, Principal user) {
    mockMvc.perform(post("/client").with(user(user.getName())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsByteArray();
  }

  private TestingAuthenticationToken userToken(String username) {
    return new TestingAuthenticationToken(username, "");
  }

  private List<Client> getClients(Principal user) throws Exception {
    byte[] result = mockMvc.perform(get("/client").with(user(user.getName())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsByteArray();

    CollectionLikeType type = json.getTypeFactory()
        .constructCollectionLikeType(ArrayList.class, Client.class);
    return json.readValue(result, type);
  }

}