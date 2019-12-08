package org.bashhead.srini.client;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.bashhead.srini.client.model.Client;
import org.bashhead.srini.client.model.RequestError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

	private final ClientDao dao;

	public ClientController(ClientDao dao) {
		this.dao = dao;
	}

	@ResponseBody
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public RequestError error(Exception e) {
    return RequestError.builder()
        .type(e.getClass().getSimpleName())
        .message(e.getMessage())
        .build();
  }

  @RequestMapping(value = "/client", method = GET)
  public List<Client> list(Principal user) {
    return dao.getAll(user);
  }

  @RequestMapping(value = "/client/{id}", method = GET)
  public Client findById(Principal user, @PathVariable long id) {
		return dao.findById(id, user);
  }

	@RequestMapping(value = "/client", method = PUT)
	public void update(Principal user, @RequestBody @Valid Client client) {
		dao.update(client, user);
	}

	@RequestMapping(value = "/client", method = POST)
	public Client create(Principal user, @RequestBody @Valid Client client) {
		return dao.create(client, user);
	}

}