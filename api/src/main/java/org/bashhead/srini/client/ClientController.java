package org.bashhead.srini.client;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.security.Principal;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.bashhead.srini.client.model.Client;
import org.bashhead.srini.client.model.RequestError;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client")
public class ClientController {

	private final ClientDao dao;

	public ClientController(ClientDao dao) {
		this.dao = dao;
	}

  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public RequestError error(Exception e) {
    return RequestError.builder()
        .type(e.getClass().getSimpleName())
        .message(e.getMessage())
        .build();
  }

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(EmptyResultDataAccessException.class)
	public RequestError notFoundError(EmptyResultDataAccessException e) {
		return RequestError.builder()
				.type(e.getClass().getSimpleName())
				.message("Entity not found")
				.build();
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(
			MethodArgumentNotValidException ex) {
		return ex.getBindingResult()
				.getFieldErrors().stream()
				.map(it -> new AbstractMap.SimpleEntry<>(it.getField(), it.getDefaultMessage()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

  @RequestMapping(value = "", method = GET)
  public List<Client> list(Principal user) {
    return dao.getAll(user);
  }

  @RequestMapping(value = "/{id}", method = GET)
  public Client findById(Principal user, @PathVariable long id) {
		return dao.findById(id, user);
  }

	@RequestMapping(value = "", method = PUT)
	public Client update(Principal user, @RequestBody @Valid Client client) {
		dao.update(client, user);
		return client;
	}

	@RequestMapping(value = "", method = POST)
	public Client create(Principal user, @RequestBody @Valid Client client) {
		return dao.create(client, user);
	}

}