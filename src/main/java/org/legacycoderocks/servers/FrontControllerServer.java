package org.legacycoderocks.servers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.legacycoderocks.commands.Command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class FrontControllerServer {

	public static void main(String[] args) throws Exception {

		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new MyHandler().with("GET", "todos", "org.legacycoderocks.commands.GetTodos")
				.with("PUT", "todos", "org.legacycoderocks.commands.PutTodos"));

		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class MyHandler implements HttpHandler {

		private ObjectMapper mapper;

		private URLMappingManager mappings = new URLMappingManager();

		public MyHandler with(String method, String path, String clazz) {
			mappings.with(method, path, clazz);
			return this;
		}

		public MyHandler() {
			mapper = new ObjectMapper();
		}

		@SuppressWarnings("unchecked")
		public void handle(HttpExchange t) throws IOException {
			OutputStream os = t.getResponseBody();
			String response = "";
			JsonNode params = mapper.readTree(t.getRequestBody());

			Command c = resolveDispatcher(t.getRequestMethod(), t.getRequestURI().getPath(),
					mapper.convertValue(params, Map.class));
			int header = 200;
			try {
				c.execute();
				response = mapper.writeValueAsString(c);
				IOUtils.write(response, os);
			} catch (Exception e) {
				header = 400;
			}

			finally {
				os.close();
			}
			t.sendResponseHeaders(header, response.length());
		}

		protected Command resolveDispatcher(String method, String path, Map<String, Object> params) {
			return mappings.getCommand(method, path, params);
		}

	}
}
