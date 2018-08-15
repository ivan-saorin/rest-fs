package org.explorer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.explorer.api.exceptions.WebApiRuntimeException;
import org.explorer.api.rest.request.parameters.KeyValue;
import org.explorer.api.rest.request.parameters.RequestParameters;

//@WebServlet(name = "explorerServlet", urlPatterns = { "/explore" }, loadOnStartup = 1)
public class ExplorerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6206286860336102055L;

	public ExplorerServlet() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final long startTime = System.currentTimeMillis();

		RequestParameters parameters = new RequestParameters(request);
		
		Collection<KeyValue> params = parameters.all().asCollection();
		System.err.println(parameters.toString());
		
		
		File baseDir = new File(getServletContext().getInitParameter("fs.path"));
		String pathInfo = request.getPathInfo();
		File file = new File(baseDir, pathInfo);
		if (file.exists()) {
			String fn = file.getName();
			String[] fnParts = fn.split("\\.");
	        response.setContentType("text/" + fnParts[1]);
	        response.setStatus(HttpServletResponse.SC_OK);
	        download(response, file);
		}
		else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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

