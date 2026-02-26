package com.cronopista.c2.erp.presave;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.cronopista.c2.utils.C2Maps;
import com.cronopista.c2.utils.PresaveListener;

@Component
public class UserPresaveListener implements PresaveListener {

	@Override
	public void presave(Map<String, Object> body) {

		if (C2Maps.getBoolean(body, "passwordChanged")) {
			String password = C2Maps.getString(body, "password");
			body.put("password", DigestUtils.sha1Hex(password));
		}

	}

}
