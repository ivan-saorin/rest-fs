package org.explorer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commonjava.mimeparse.MIMEParse;
import org.explorer.api.exceptions.WebApiRuntimeException;
import org.explorer.api.rest.request.parameters.KeyValue;
import org.explorer.api.rest.request.parameters.RequestParameters;
import org.explorer.api.rest.request.parameters.Type;
import org.explorer.api.utils.Constants;

//@WebServlet(name = "explorerServlet", urlPatterns = { "/explore" }, loadOnStartup = 1)
public class ExplorerServlet extends HttpServlet {

	private static final long serialVersionUID = -6206286860336102055L;

	private static final List<String> SUPPORTED_MIMETYPE = 
			Arrays.asList(
					new String[] {"application/json", "application/xml"}
				); 

	private static final Charset ENCODING = StandardCharsets.UTF_8;
	private static final String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	public ExplorerServlet() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final long startTime = System.currentTimeMillis();

		File baseDir = new File(getServletContext().getInitParameter("fs.path"));

		RequestParameters parameters = new RequestParameters(request);				
		System.err.println(parameters.toString());
		
		KeyValue kvAccept = parameters.filter(Type.HEADER).asMap().get("Accept");
		String accept = null;
		if (kvAccept != null) {
			accept = kvAccept.getValue();
		}
		
		String pathInfo = request.getPathInfo();
		if (accept != null) {
			System.err.print("Supported MimeType=");
			System.err.println(SUPPORTED_MIMETYPE);
			String bestMatch = MIMEParse.bestMatch(SUPPORTED_MIMETYPE, accept);
			System.err.print("Best match=");
			System.err.println(bestMatch);			
			get(response, baseDir, pathInfo, bestMatch, accept, SUPPORTED_MIMETYPE);			
		}
		else {
			get(response, baseDir, pathInfo, "application/json", null, SUPPORTED_MIMETYPE);
		}
		
		
		

		/*
		System.out.println("filesystem path: " + baseDir.getAbsolutePath());
		if (baseDir.exists() && baseDir.isDirectory()) {
			String[] list = baseDir.list();
			for (String fd : list) {
				File f = new File(baseDir, fd);
				if (f.isDirectory()) {
					System.out.println("D " + f.getAbsolutePath());
				}
				else {
					System.out.println("F " + f.getAbsolutePath());
				}
			}
		}
		*/
    }

	private void get(HttpServletResponse response, File baseDir, String pathInfo, String bestMatch, String accept, List<String> supportedMimeType) throws IOException {
		File file = new File(baseDir, pathInfo);
		if (file.exists() && file.isDirectory()) {
			getDir(response, file, bestMatch, accept, supportedMimeType);
		}
		else {
			getFile(response, file, bestMatch, accept, supportedMimeType);
		}
		
	}
	
	private void getDir(HttpServletResponse response, File dir, String bestMatch, String accept, List<String> supportedMimeType) throws IOException {
		String ext = getExtension(bestMatch);
		File[] files = getFiles(dir, ext);
		if (files.length > 0) {
			dirResponse(response, bestMatch, files);
		} else {
			for (String mime : supportedMimeType) {
				if (mime.equals(bestMatch)) {
					continue;
				}
				ext = getExtension(mime);
				files = getFiles(dir, ext);
				if (files.length > 0) {
					dirResponse(response, mime, files);
					break;
				}			
			}			
		}
	}

	private void dirResponse(HttpServletResponse response, String mime, File[] files) throws IOException {
		if (mime.equals("application/json")) {
			jsonDirResponse(response, files);
		}
		else if (mime.equals("application/xml")) {
			xmlDirResponse(response, files);
		}		
	}

	private void jsonDirResponse(HttpServletResponse response, File[] files) throws IOException {
		OutputStream out = response.getOutputStream();
		write(out, "[");
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String content = getFileContent(file, ENCODING);
			write(out, content);
			if (i < files.length - 1) {
				write(out, ",");
			}
		}
		write(out, "]");
	}

	private void xmlDirResponse(HttpServletResponse response, File[] files) throws IOException {
		OutputStream out = response.getOutputStream();
		write(out, XML_PREFIX);
		write(out, Constants.EOL);
		write(out, "<elements>");
		write(out, Constants.EOL);
		for (File file : files) {
			String content = getFileContent(file, ENCODING);
			if (content.startsWith(XML_PREFIX)) {
				content.substring(XML_PREFIX.length() + 1);
			}
			write(out, Constants.EOL);
		}
		write(out, "</elements>");
		write(out, Constants.EOL);		
	}

	private String getFileContent(File file, Charset encoding) throws IOException {
		  byte[] encoded = Files.readAllBytes(file.toPath());
		  return new String(encoded, encoding);
	}
	
	private void write(OutputStream out, String line) throws IOException {
		byte[] bytes = line.getBytes(ENCODING);
		out.write(bytes, 0, bytes.length);
	}
	
	private File[] getFiles(File dir, String ext) {
		String[] files = dir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(ext);
			}
			
		});
		
		File[] res = new File[files.length];
		int i = 0;
		for (String file : files) {
			res[i++] = new File(dir, file);
		}
		return res;
	}

	private void getFile(HttpServletResponse response, File file, String bestMatch, String accept, List<String> supportedMimeType) {
		String contentType = getContentType(file, bestMatch, accept, supportedMimeType);
		
		File baseDir = file.getParentFile();
		String fn = file.getName();
		String ext = getExtension(contentType);		
		File f = getFile(baseDir, fn, ext);
		
		if (f == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		else if (f.exists()) {
	        response.setContentType(contentType);
	        response.setStatus(HttpServletResponse.SC_OK);
	        download(response, f);
		}
		else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}		
	}

	private String getContentType(File file, String bestMatch, String accept, List<String> supportedMimeType) {
		File baseDir = file.getParentFile();
		String fn = file.getName();
		String ext = getExtension(bestMatch);
		File f = getFile(baseDir, fn, ext);
		if (f.exists()) {
			return bestMatch;
		}
		
		for (String mime : supportedMimeType) {
			if (mime.equals(bestMatch)) {
				continue;
			}
			ext = getExtension(mime);
			f = getFile(baseDir, fn, ext);
			if (f.exists()) {
				return mime;
			}			
		}
		
		return null;
	}

	private File getFile(File baseDir, String fn, String ext) {
		return new File(baseDir, fn + "." + ext);
	}

	private String getExtension(String mime) {
		String[] mimeParts = mime.split("/");
		return mimeParts[1];
	}

	private void download(HttpServletResponse response, File file) {
		try {
			OutputStream out = response.getOutputStream();
			FileInputStream in = new FileInputStream(file);
			byte[] buffer = new byte[4096];
			int length;
			while ((length = in.read(buffer)) > 0){
			    out.write(buffer, 0, length);
			}
			in.close();
			out.flush();
		} catch (IOException cause) {
			throw new WebApiRuntimeException(cause);
		}		
	}
}

