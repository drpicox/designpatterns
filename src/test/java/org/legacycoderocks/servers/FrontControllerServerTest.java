package org.legacycoderocks.servers;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.legacycoderocks.commands.Command;
import org.mockito.Mockito;

import com.sun.net.httpserver.HttpExchange;

public class FrontControllerServerTest {

	@Test
	public void when_put_todo_then_insert_on_redis() throws Exception {

		final Command command = Mockito.mock(Command.class);

		FrontControllerServer.MyHandler handler = new FrontControllerServer.MyHandler() {
			protected Command resolveDispatcher(String method, String path, Map<String, Object> params) {
				return command;
			}
		};
		HttpExchange http = Mockito.mock(HttpExchange.class);
		Mockito.when(http.getRequestURI()).thenReturn(new URI("http://localhost/todos"));
		Mockito.when(http.getRequestMethod()).thenReturn("PUT");

		OutputStream os = Mockito.mock(OutputStream.class);

		Mockito.when(http.getResponseBody()).thenReturn(os);
		String task = "{ \"task\" : \"I need to refactor this\" }";
		InputStream stubInputStream = IOUtils.toInputStream(task);

		Mockito.when(http.getRequestBody()).thenReturn(stubInputStream);

		handler.handle(http);
		Mockito.verify(command, Mockito.times(1)).execute();
	}

}
