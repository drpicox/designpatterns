package org.legacycoderocks.servers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

public class MonolythicTodoServer {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new MyHandler(System.getenv("REDIS_PORT")));
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class MyHandler implements HttpHandler {

		private Jedis redisClient;

		private ObjectMapper mapper;

		public MyHandler() {
			mapper = new ObjectMapper();
		}

		public MyHandler(String redisURL) throws MalformedURLException {
			this();
			String hostPortURL = redisURL.substring("tcp://".length());
			int separator = hostPortURL.indexOf(':');
			setDB(new Jedis(hostPortURL.substring(0, separator),
					Integer.parseInt(hostPortURL.substring(separator + 1))));

		}

		public void setDB(Jedis redisClient) {
			this.redisClient = redisClient;
		}

		public void handle(HttpExchange t) throws IOException {
			String method = t.getRequestMethod();
			OutputStream os = t.getResponseBody();
			String response = "";
			try {
				if (t.getRequestURI().getPath().equals("/todos")) {
					if (method.equals("GET")) {
						ScanResult<String> cursor = redisClient.scan("todos");
						List<String> tasks = cursor.getResult();
						response = mapper.writeValueAsString(tasks);

					} else if (method.equals("PUT")) {
						JsonNode params = mapper.readTree(t.getRequestBody());
						redisClient.sadd("todos", params.get("task").asText());
					}
				}

				t.sendResponseHeaders(200, response.length());
			} finally {
				os.close();
			}
		}

		@Override
		public void finalize() {
			redisClient.close();
			redisClient.shutdown();
		}
	}
}