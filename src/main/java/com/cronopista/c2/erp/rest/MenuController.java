package com.cronopista.c2.erp.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cronopista.c2.data.services.UserService;
import com.cronopista.c2.erp.model.MenuItem;
import com.cronopista.c2.security.C2SecurityToken;
import com.cronopista.c2.security.SecuredService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Controller
@RequestMapping(path = "/menu")
public class MenuController {

	@Value("classpath:configurations/menu.json")
	Resource menuResource;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserService userService;

	MenuItem menu;

	@PostConstruct
	public void init() {
		try {
			menu = objectMapper.readValue(menuResource.getContentAsByteArray(), MenuItem.class);

		} catch (IOException e) {

			throw new RuntimeException("Error loading menu", e);
		}

	}

	@GetMapping
	@SecuredService
	public ResponseEntity<MenuItem> menu(C2SecurityToken token) {

		MenuItem userMenu = objectMapper.convertValue(menu, MenuItem.class);
		removeItemWithNoAccess(userMenu, userService.getUserPermissions(token.get(C2SecurityToken.USER_ID)));

		return new ResponseEntity<MenuItem>(userMenu, HttpStatus.OK);
	}

	private void removeItemWithNoAccess(MenuItem menu, Set<String> permissions) {
		List<MenuItem> toRemove = new ArrayList<>();
		for (MenuItem item : menu.getChildren()) {
			boolean hasAccess = item.getChildren().size() > 0;
			for (String access : item.getAccess()) {
				if (permissions.contains(access)) {
					hasAccess = true;
				}
			}

			if (!hasAccess) {
				toRemove.add(item);
			} else if (item.getChildren().size() > 0) {
				removeItemWithNoAccess(item, permissions);
				if (item.getAccess().size() == 0 && item.getChildren().size() == 0) {
					toRemove.add(item);
				}
			}

		}
		menu.getChildren().removeAll(toRemove);

	}

	@GetMapping("/all")
	public ResponseEntity<MenuItem> menu() {

		return new ResponseEntity<MenuItem>(menu, HttpStatus.OK);
	}

}
