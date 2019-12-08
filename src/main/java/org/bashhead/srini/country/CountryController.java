package org.bashhead.srini.country;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import org.bashhead.srini.country.model.Country;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountryController {

  private final CountryDao dao;

  public CountryController(CountryDao dao) {
    this.dao = dao;
  }

  @RequestMapping(value = "/country", method = GET)
  public List<Country> list() {
    return dao.getAll();
  }
}
