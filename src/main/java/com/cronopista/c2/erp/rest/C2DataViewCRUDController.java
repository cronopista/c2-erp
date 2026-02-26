package com.cronopista.c2.erp.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cronopista.c2.data.C2DataViewDAO;
import com.cronopista.c2.data.C2DataViewLoader;
import com.cronopista.c2.data.SearchOptions;
import com.cronopista.c2.data.SearchResults;
import com.cronopista.c2.data.model.dataview.C2View;
import com.cronopista.c2.security.C2SecurityToken;
import com.cronopista.c2.security.SecuredService;

@Controller
@RequestMapping(path = "/data")
public class C2DataViewCRUDController {

	@Autowired
	C2DataViewLoader viewLoader;

	@Autowired
	C2DataViewDAO c2DataViewDAO;

	@GetMapping("/{view}/{id}")
	@SecuredService(hasView = true)
	public ResponseEntity<Map<String, Object>> getOne(C2SecurityToken token, @PathVariable String view,
			@PathVariable String id) {

		C2View c2View = viewLoader.getView(view);

		if (c2View.getChildReference() != null) {
			String parentId = token.get(c2View.getParentReference().substring(2));

			return new ResponseEntity<>(
					c2DataViewDAO.getOne(view, "WHERE id = ? AND " + c2View.getChildReference() + " = ?", id, parentId),
					HttpStatus.OK);
		}

		return new ResponseEntity<>(c2DataViewDAO.getOne(view, "WHERE id = ? ", id), HttpStatus.OK);
	}

	@PostMapping("/search/{view}")
	@SecuredService(hasView = true)
	public ResponseEntity<SearchResults> find(C2SecurityToken token, @PathVariable String view,
			@RequestBody(required = false) SearchOptions searchOptions) {

		C2View c2View = viewLoader.getView(view);

		if (c2View.getChildReference() != null) {
			String parentId = token.get(c2View.getParentReference().substring(2));

			return new ResponseEntity<>(
					c2DataViewDAO.find(view, "WHERE " + c2View.getChildReference() + " = ?", searchOptions, parentId),
					HttpStatus.OK);
		}

		return new ResponseEntity<>(c2DataViewDAO.find(view, null, searchOptions), HttpStatus.OK);
	}

	@DeleteMapping("/{view}/{id}")
	@SecuredService(hasView = true, isWriteAccess = true)
	public ResponseEntity<Void> delete(C2SecurityToken token, @PathVariable String view, @PathVariable String id,
			@RequestBody(required = false) SearchOptions searchOptions) {

		// TODO enforce parent Id

		c2DataViewDAO.delete(view, id, searchOptions);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{view}")
	@SecuredService(hasView = true, isWriteAccess = true)
	public ResponseEntity<Void> deleteQuery(C2SecurityToken token, @PathVariable String view,
			@RequestBody(required = false) SearchOptions searchOptions) {

		c2DataViewDAO.delete(view, null, searchOptions);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{view}/all/{ids}")
	@SecuredService(hasView = true, isWriteAccess = true)
	public ResponseEntity<Void> deleteAll(C2SecurityToken token, @PathVariable String view,
			@PathVariable List<String> ids) {

		c2DataViewDAO.deleteAll(view, ids);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{view}")
	@SecuredService(hasView = true, isWriteAccess = true)
	public ResponseEntity<Map<String, Object>> write(C2SecurityToken token, @PathVariable String view,
			@RequestBody Map<String, Object> body) {

		C2View c2View = viewLoader.getView(view);

		if (c2View.getChildReference() != null && c2View.getParentReference().startsWith("$.")) {
			String parentId = token.get(c2View.getParentReference().substring(2));
			body.put(c2View.getChildReference(), parentId);

		}

		body = c2DataViewDAO.write(view, body);

		return new ResponseEntity<>(body, HttpStatus.OK);
	}

}
