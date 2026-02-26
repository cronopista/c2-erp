package com.cronopista.c2.erp.rest;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cronopista.c2.data.C2DataViewDAO;
import com.cronopista.c2.security.C2SecurityToken;
import com.cronopista.c2.utils.C2Maps;
import com.cronopista.c2.utils.EncryptionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(path = "/login")
public class LoginController {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private C2DataViewDAO dao;

	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping
	public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password)
			throws JsonProcessingException {

		String hashedPassword = DigestUtils.sha1Hex(password);
		Map<String, Object> user = dao.getOne("users",
				"WHERE login LIKE ? and password=? and (nextAttempt is null or NOW() > nextAttempt)", username,
				hashedPassword);
		if (user == null) {
			user = dao.getOne("users", "WHERE login LIKE ?", username);
			int attempts = 0;
			Date nextAttempt = null;
			if (user != null) {
				attempts = C2Maps.getInt(user, "attempts");
				attempts++;
				Calendar cal=GregorianCalendar.getInstance();
				cal.add(Calendar.MILLISECOND, (int)Math.pow(2, attempts));
				nextAttempt = cal.getTime();
				user.put("nextAttempt", nextAttempt);
				user.put("attempts", attempts);
				dao.write("users", user);

			}
			log.error("Failed login with user: '" + username + "', pasword: '" + password + "', attempts: " + attempts
					+ ", next attempt: " + nextAttempt);
			return new ResponseEntity<>(user, HttpStatus.UNAUTHORIZED);
		} else {
			user.put("nextAttempt", null);
			user.put("attempts", 0);
			dao.write("users", user);
		}

		String userId = String.valueOf(C2Maps.getLong(user, "id"));

		String securityToken = objectMapper.writeValueAsString(new C2SecurityToken(userId));
		String encryptedToken = EncryptionUtils.encrypt(securityToken);
		user.put("securityToken", encryptedToken);

		return new ResponseEntity<>(user, HttpStatus.OK);
	}

}
