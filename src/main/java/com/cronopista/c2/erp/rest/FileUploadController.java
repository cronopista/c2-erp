package com.cronopista.c2.erp.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cronopista.c2.data.C2DataViewDAO;
import com.cronopista.c2.security.C2SecurityToken;
import com.cronopista.c2.security.SecuredService;
import com.cronopista.c2.utils.C2Maps;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(path = "/file")
public class FileUploadController {

	private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

	@Value("${file.upload.path:/erp/uploads}")
	private String baseFilePath;

	private Path rootLocation;

	@Autowired
	private C2DataViewDAO dao;

	@PostConstruct
	public void init() throws IOException {
		rootLocation = Paths.get(baseFilePath);
//		File file = rootLocation.toFile();
//		if (!file.exists()) {
//			Files.createDirectories(rootLocation);
//		}
	}

	@PostMapping("/{view}/upload")
	@SecuredService(hasView = true, isWriteAccess = true)
	public ResponseEntity<Map<String, Object>> upload(C2SecurityToken token, @PathVariable String view,
			@RequestParam MultipartFile file) throws IOException {

		log.info("Uploading file " + file.getOriginalFilename() + " to " + view);
		String newFilename = UUID.randomUUID().toString();
		Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();
		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
		}

		Map<String, Object> fileEntry = C2Maps.of("path", destinationFile.toString(), "name",
				file.getOriginalFilename(), "view", view);
		Map<String, Object> fileObject = dao.write("file-uploads", fileEntry);
		fileObject.remove("path");

		return new ResponseEntity<>(fileObject, HttpStatus.OK);
	}

	@DeleteMapping("/remove/{view}/{fileId}")
	@SecuredService(hasView = true, isWriteAccess = true)
	public ResponseEntity<Void> delete(C2SecurityToken token, @PathVariable String view, @PathVariable String fileId)
			throws IOException {

		Map<String, Object> fileData = dao.getOne("file-uploads", "WHERE id=? AND view=?", fileId, view);
		if (fileData == null) {
			log.error("File to delete not found. Id: " + fileId);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		File file = new File(C2Maps.string(fileData, "path"));
		file.delete();
		dao.delete("file-uploads", fileId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{view}/{fileId}")
	public void get(@PathVariable String view, @PathVariable Number fileId,
			HttpServletRequest req, HttpServletResponse response) throws IOException {

		Map<String, Object> fileData = dao.getOne("file-uploads", "WHERE id=? AND view=?", fileId, view);
		if (fileData == null) {
			log.error("File not found. Id: " + fileId);
			return;
		}
		File file = new File(C2Maps.string(fileData, "path"));
		String filename = C2Maps.string(fileData, "name");
		String mimeType = URLConnection.guessContentTypeFromName(filename);

		response.setContentType(mimeType);
		response.setHeader("Content-disposition", "attachment; filename=" + filename);
		response.setContentLength((int) file.length());

		ServletOutputStream os = response.getOutputStream();
		byte[] bufferData = new byte[1024];
		int read = 0;
		InputStream fis = new FileInputStream(file);
		while ((read = fis.read(bufferData)) != -1) {
			os.write(bufferData, 0, read);
		}
		os.flush();
		os.close();
		fis.close();

	}

	@GetMapping("/info/{view}/{fileId}")
	@SecuredService(hasView = true)
	public ResponseEntity<Map<String, Object>> getFileInfo(C2SecurityToken token, @PathVariable String view,
			@PathVariable Number fileId, HttpServletRequest req, HttpServletResponse response) throws IOException {
		Map<String, Object> fileData = dao.getOne("file-uploads", "WHERE id=? AND view=?", fileId, view);

		return new ResponseEntity<>(fileData, HttpStatus.OK);
	}

}
