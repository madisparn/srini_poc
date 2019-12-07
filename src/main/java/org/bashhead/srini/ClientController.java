package org.bashhead.srini;

import java.util.Arrays;
import java.util.List;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/client")
public class ClientController {
	
	@RequestMapping("/")
	@PostFilter("filterObject.assignee == authentication.name")
	public List<Client> list() {
		return Arrays.asList(
		);
	}
	
}