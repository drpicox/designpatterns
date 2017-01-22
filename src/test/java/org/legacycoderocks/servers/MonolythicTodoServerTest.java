package org.legacycoderocks.servers;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.net.httpserver.HttpExchange;

import redis.clients.jedis.Jedis;

public class MonolythicTodoServerTest {

	@Test
	public void when_put_todo_then_insert_on_redis() throws Exception {

		MonolythicTodoServer.MyHandler handler = new MonolythicTodoServer.MyHandler();
		HttpExchange http = Mockito.mock(HttpExchange.class);
		Mockito.when(http.getRequestURI()).thenReturn(new URI("http://localhost/todos"));
		Mockito.when(http.getRequestMethod()).thenReturn("PUT");
		
		OutputStream os = Mockito.mock(OutputStream.class);
		
		Mockito.when(http.getResponseBody()).thenReturn(os);
		String task = "{ \"task\" : \"I need to refactor this\" }";
		InputStream stubInputStream = IOUtils.toInputStream(task);

		Mockito.when(http.getRequestBody()).thenReturn(stubInputStream);

		Jedis jedis = Mockito.mock(Jedis.class);
		handler.setDB(jedis);
		handler.handle(http);
		Mockito.verify(jedis, Mockito.times(1)).sadd(Mockito.eq("todos"), Mockito.anyString());
	}

}
