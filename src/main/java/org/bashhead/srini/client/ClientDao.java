package org.bashhead.srini.client;

import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.bashhead.srini.client.model.Client;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
public class ClientDao {

  private final NamedParameterJdbcTemplate jdbc;

  public ClientDao(DataSource ds) {
    this.jdbc = new NamedParameterJdbcTemplate(ds);
  }

  public List<Client> getAll(Principal user) {
    String sql = "select * from client where assignee = :user";
    return jdbc.query(sql,
        Collections.singletonMap("user", user.getName()),
        this::clientRowMapper);
  }

  public Client findById(long id, Principal user) {
    String sql = "select * from client where id = :id and assignee = :user";
    return jdbc.queryForObject(sql,
        new MapSqlParameterSource()
            .addValue("user", user.getName())
            .addValue("id", id),
        this::clientRowMapper);
  }

  private Client clientRowMapper(ResultSet rs, int idx) throws SQLException {
    return Client.builder()
        .id(rs.getLong("id"))
        .firstName(rs.getString("first_name"))
        .lastName(rs.getString("last_name"))
        .username(rs.getString("username"))
        .email(rs.getString("email"))
        .address(rs.getString("address"))
        .countryId(rs.getLong("country_id"))
        .build();
  }

  public void update(Client client, Principal user) {
    String sql = "update client set "
        + "first_name = :first_name, "
        + "last_name = :last_name, "
        + "username = :username, "
        + "email = :email, "
        + "address = :address, "
        + "country_id = :country_id, "
        + "where id = :id and assignee = :assignee";
    jdbc.update(sql, clientParameters(client)
        .addValue("assignee", user.getName()));
  }

  public Client create(Client client, Principal user) {
    String sql = "insert into client "
        + "(first_name, last_name, username, email, address, country_id, assignee) values "
        + "(:first_name, :last_name, :username, :email, :address, :country_id, :assignee) ";
    KeyHolder kh = new GeneratedKeyHolder();
    jdbc.update(sql, clientParameters(client)
            .addValue("assignee", user.getName()),
        kh
    );

    return client.toBuilder()
        .id(kh.getKey().longValue())
        .build();
  }

  private MapSqlParameterSource clientParameters(Client client) {
    return new MapSqlParameterSource()
        .addValue("first_name", client.getFirstName())
        .addValue("last_name", client.getLastName())
        .addValue("username", client.getUsername())
        .addValue("email", client.getEmail())
        .addValue("address", client.getAddress())
        .addValue("country_id", client.getCountryId());
  }

}
