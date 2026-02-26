package com.cronopista.c2.erp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cronopista.c2.data.C2DataViewLoader;
import com.cronopista.c2.data.model.dataview.C2View;
import com.cronopista.c2.security.SecuredService;

@Controller
@RequestMapping(path = "/views")
public class C2ViewController {

	@Autowired
	C2DataViewLoader viewLoader;

	@GetMapping("/{view}")
	@SecuredService
	public ResponseEntity<C2View> view(@PathVariable String view) {

		return new ResponseEntity<>(viewLoader.getView(view), HttpStatus.OK);
	}

}
