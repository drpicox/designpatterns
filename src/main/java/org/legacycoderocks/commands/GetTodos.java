package org.legacycoderocks.commands;

import java.util.List;

import org.legacycoderocks.redis.Redis;

import redis.clients.jedis.ScanResult;

public class GetTodos implements Command {

	private List<String> tasks;

	public void execute() throws Exception {
		ScanResult<String> cursor = Redis.getRedis().getJedis().scan("todos");
		tasks = cursor.getResult();
	}

	public List<String> getTask() {
		return tasks;
	}

}
