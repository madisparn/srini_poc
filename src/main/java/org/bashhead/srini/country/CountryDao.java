package org.bashhead.srini.country;

import java.util.List;
import javax.sql.DataSource;
import org.bashhead.srini.country.model.Country;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CountryDao {

  private final JdbcTemplate tmpl;

  public CountryDao(DataSource ds) {
    this.tmpl = new JdbcTemplate(ds);
  }

  public List<Country> getAll() {
    String sql = "select * from country";
    return tmpl.query(sql, (rs, idx) -> Country.builder()
        .id(rs.getLong("id"))
        .name(rs.getString("name"))
        .build());
  }
}
